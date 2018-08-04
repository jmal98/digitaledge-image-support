package com.deleidos.rtws.ami.preconditions;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagement;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClient;
import com.amazonaws.services.identitymanagement.model.CreateAccessKeyRequest;
import com.amazonaws.services.identitymanagement.model.CreateAccessKeyResult;
import com.amazonaws.services.identitymanagement.model.CreateUserRequest;
import com.amazonaws.services.identitymanagement.model.CreateUserResult;
import com.amazonaws.services.identitymanagement.model.DeleteAccessKeyRequest;
import com.amazonaws.services.identitymanagement.model.DeleteSigningCertificateRequest;
import com.amazonaws.services.identitymanagement.model.DeleteUserPolicyRequest;
import com.amazonaws.services.identitymanagement.model.DeleteUserRequest;
import com.amazonaws.services.identitymanagement.model.GetUserRequest;
import com.amazonaws.services.identitymanagement.model.GetUserResult;
import com.amazonaws.services.identitymanagement.model.ListSigningCertificatesRequest;
import com.amazonaws.services.identitymanagement.model.ListSigningCertificatesResult;
import com.amazonaws.services.identitymanagement.model.ListUserPoliciesRequest;
import com.amazonaws.services.identitymanagement.model.ListUserPoliciesResult;
import com.amazonaws.services.identitymanagement.model.ListUsersResult;
import com.amazonaws.services.identitymanagement.model.PutUserPolicyRequest;
import com.amazonaws.services.identitymanagement.model.SigningCertificate;
import com.amazonaws.services.identitymanagement.model.UploadSigningCertificateRequest;
import com.amazonaws.services.identitymanagement.model.UploadSigningCertificateResult;
import com.amazonaws.services.identitymanagement.model.User;
import com.deleidos.rtws.ami.model.AmiCreationPreconditions;
import com.deleidos.rtws.ami.model.Constants;
import com.deleidos.rtws.ami.model.IamUserData;
import com.deleidos.rtws.ami.utils.CredentialsManagement;

@Component
public class IamUserIntialized extends Thread implements AmiCreationPreconditions<IamUserData> {

	private Logger logger = LoggerFactory.getLogger(getClass());

	//@Autowired
	//private CredentialsManagement credentialsMgr;

	private IamUserData iamUserData = null;

	private AWSCredentials savedCredentials = null;

	private String userNameToUse = Constants.IAM_USERNAME;


	public void removeIamUser(final AWSCredentials credentials, IamUserData user) {

		if (user != null) {

			AmazonIdentityManagementClient iam = null;
			try {
				iam = new AmazonIdentityManagementClient(new AWSCredentials() {

					public String getAWSSecretKey() {

						return credentials.getAWSSecretKey();
					}

					public String getAWSAccessKeyId() {

						return credentials.getAWSAccessKeyId();
					}
				});

				GetUserResult usrToDelete = iam.getUser(new GetUserRequest().withUserName(user.getUsername()));
				logger.info("Deleting IAM user:" + usrToDelete.getUser().getUserName());

				if (user.getCertId() != null)
					iam.deleteSigningCertificate(new DeleteSigningCertificateRequest().withUserName(user.getUsername()).withCertificateId(user.getCertId()));

				ListUserPoliciesResult policies = iam.listUserPolicies(new ListUserPoliciesRequest(user.getUsername()));

				for (String policyName : policies.getPolicyNames()) {
					iam.deleteUserPolicy(new DeleteUserPolicyRequest(user.getUsername(), policyName));
				}

				iam.deleteAccessKey(new DeleteAccessKeyRequest().withUserName(user.getUsername()).withAccessKeyId(user.getAccessKey()));
				iam.deleteUser(new DeleteUserRequest(user.getUsername()));
			} finally {
				if (iam != null)
					iam.shutdown();
			}
		} else {
			logger.warn("IAM User cache did not contain the any data.");
		}
	}

	public IamUserData execute(AWSCredentials credentials) {

		if (savedCredentials == null)
			savedCredentials = credentials;
		
		CredentialsManagement credentialsMgr = new CredentialsManagement();

		AmazonIdentityManagement iam = null;
		iamUserData = new IamUserData();

		try {
			File pk = credentialsMgr.generatePk();
			iamUserData.setPk(pk);

			File cert = credentialsMgr.generateCert(pk);
			iamUserData.setCert(cert);

			String certStr = IOUtils.toString(new URI("file:///" + cert.getAbsolutePath().replace('\\', '/')), "UTF-8");

			iam = new AmazonIdentityManagementClient(credentials);

			if (!isUserCreated(iam)) {

				CreateUserResult createResult = iam.createUser(new CreateUserRequest(userNameToUse));

				iamUserData.setUsername(createResult.getUser().getUserName());

				if (logger.isDebugEnabled())
					logger.debug("IAM User Creation Result:" + createResult);
			}

			String policyDocument = "{\"Statement\":[{\"Effect\":\"Allow\",\"Action\":\"*\",\"Resource\":\"*\"}]}";
			PutUserPolicyRequest userPolicy = new PutUserPolicyRequest()
					.withUserName(userNameToUse)
					.withPolicyDocument(policyDocument).withPolicyName("root");
			iam.putUserPolicy(userPolicy);

			CreateAccessKeyResult accessKeyRequest = iam
					.createAccessKey(new CreateAccessKeyRequest()
							.withUserName(userNameToUse));
			iamUserData.setAccessKey(accessKeyRequest.getAccessKey()
					.getAccessKeyId());
			iamUserData.setSecretKey(accessKeyRequest.getAccessKey()
					.getSecretAccessKey());
			
			System.out.println("PARAMETERS: access with length "+iamUserData.getAccessKey().length());
			System.out.println("PARAMETERS: secret with length "+iamUserData.getSecretKey().length());

			if (logger.isDebugEnabled())
				logger.debug("" + accessKeyRequest);

			if (!has509Cert(iam)) {
				UploadSigningCertificateRequest uploadSigningCertificateRequest = new UploadSigningCertificateRequest()
						.withUserName(userNameToUse).withCertificateBody(
								certStr);
				UploadSigningCertificateResult certUploadResult = iam
						.uploadSigningCertificate(uploadSigningCertificateRequest);
				iamUserData.setCertId(certUploadResult.getCertificate()
						.getCertificateId());

				// Delay to allow for AWS IAM consistency
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					logger.error(e.getMessage());
				}

				if (logger.isDebugEnabled())
					logger.debug("" + certUploadResult);

			}

			// Add hook to ensure this central shared user is removed on jvm
			// exit
			Runtime.getRuntime().addShutdownHook(this);

			// Delay to allow for AWS IAM consistency
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				logger.error(e.getMessage());
			}

		} catch (AmazonServiceException e) {
			logger.error(e.getMessage(), e);
		} catch (AmazonClientException e) {
			logger.error(e.getMessage(), e);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		} catch (URISyntaxException e) {
			logger.error(e.getMessage(), e);
		} finally {
			if (iam != null)
				iam.shutdown();
		}

		return iamUserData;
	}

	private boolean has509Cert(AmazonIdentityManagement iam) {
		boolean hasCert = false;
		ListSigningCertificatesResult signingCerts = iam.listSigningCertificates(new ListSigningCertificatesRequest().withUserName(iamUserData.getUsername()));

		for (SigningCertificate cert : signingCerts.getCertificates()) {
			iamUserData.setCertId(cert.getCertificateId());
			hasCert = true;
		}
		return hasCert;
	}

	private boolean isUserCreated(AmazonIdentityManagement iam) {
		boolean isCreated = false;

		ListUsersResult iamUsers = iam.listUsers();
		for (User user : iamUsers.getUsers()) {
			if (logger.isDebugEnabled())
				logger.debug("USER:" + user);

			if (user.getUserName().equals(userNameToUse))
				isCreated = true;

		}
		return isCreated;
	}

}
