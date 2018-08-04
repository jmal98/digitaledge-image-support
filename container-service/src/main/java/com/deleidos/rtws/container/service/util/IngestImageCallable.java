package com.deleidos.rtws.container.service.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.deleidos.rtws.container.service.model.ImageBuildRequest;
import com.deleidos.rtws.container.service.model.PlaybookParams;

public class IngestImageCallable extends GenericSystemBaseImageDependentCallable {

	private static class IngestImageSetupUtil implements BuildAreaSetupUtil {

		private Logger logger = LoggerFactory.getLogger(IngestImageCallable.class);

		@Override
		public File setupBuildTarget(ImageBuildRequest buildRequest) {
			File target = new File(String.format("%s/%s", buildRequest.getBuildArea().getAbsolutePath(), "ingest"));
			if (!target.exists())
				target.mkdir();

			InputStream is = null;
			try {
				is = Thread.currentThread().getContextClassLoader()
						.getResourceAsStream("dockerfiles/Dockerfile_centos_system_ingest");

				// Copy the Dockerfile and update the base image reference
				File dockerfile = new File(target.getAbsolutePath() + File.separatorChar + "Dockerfile");
				DockerfileUtil.updateFromDeclaration(buildRequest.getDomain(), buildRequest.getSoftwareVersion(), is,
						dockerfile);

				// Copy the playbook
				Path playbook = FileSystems.getDefault().getPath("/tmp/ingest.tar.gz");
				Files.copy(playbook, FileSystems.getDefault()
						.getPath(target.getAbsolutePath() + File.separatorChar + "ingest.tar.gz"));

				// Write the params
				PlaybookParams params = new PlaybookParams(buildRequest.getSoftwareVersion(), buildRequest.getDomain());
				YamlMapperCache.mapper
						.writeValue(new File(target.getAbsolutePath() + File.separatorChar + "params.yml"), params);

				// Copy (as links are forbidden by a docker build) repo.zip for
				// use
				// by Dockerfile
				Files.copy(
						FileSystems.getDefault()
								.getPath(buildRequest.getBuildArea().getAbsolutePath() + File.separatorChar
										+ "repo.zip"),
						FileSystems.getDefault().getPath(target.getAbsolutePath() + File.separatorChar + "repo.zip"));

				List<String> s2iArtfcts = RepositoryZipProcessingUtil.containsS2iArtifacts(FileSystems.getDefault()
						.getPath(target.getAbsolutePath() + File.separatorChar + "repo.zip").toFile());

				if (s2iArtfcts.size() > 0) {

					RepositoryZipProcessingUtil.deleteFromZip(FileSystems.getDefault()
							.getPath(target.getAbsolutePath() + File.separatorChar + "repo.zip").toFile(), s2iArtfcts);
				}
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			} finally {
				if (is != null)
					try {
						is.close();
					} catch (IOException e) {
						logger.error(e.getMessage(), e);
					}
			}

			return target;
		}
	}

	public IngestImageCallable(ImageBuildRequest buildRequest) {
		super(buildRequest, new IngestImageSetupUtil(), String.format("%s/%s:%s_%s", "digitaledge", buildRequest.getImageName(),
				"ingest", buildRequest.getSoftwareVersion()));
	}
}
