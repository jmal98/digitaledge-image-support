package com.deleidos.rtws.ami.preconditions;

import org.springframework.stereotype.Component;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.CreateSecurityGroupRequest;
import com.amazonaws.services.ec2.model.CreateSecurityGroupResult;
import com.amazonaws.services.ec2.model.DescribeSecurityGroupsResult;
import com.amazonaws.services.ec2.model.SecurityGroup;
import com.deleidos.rtws.ami.model.AmiCreationPreconditions;

@Component
public class SecurityGroupsCreated implements AmiCreationPreconditions<Boolean> {

	public static final String AMI_CUSTOMIZATION_SG_NAME = "de-ami-creation2";
	public static final String AMI_CUSTOMIZATION_SG_DESCRIPTION = "SG Used for Digital Edge Ami Creation";

	public Boolean execute(AWSCredentials credentials) {
		Boolean success = new Boolean(false);
		boolean sgExist = false;
		AmazonEC2 ec2 = null;

		try {
			ec2 = new AmazonEC2Client(credentials);

			DescribeSecurityGroupsResult describeGroupsRslt = ec2.describeSecurityGroups();
			for (SecurityGroup sg : describeGroupsRslt.getSecurityGroups()) {
				if (sg.getGroupName() != null && sg.getDescription() != null) {
					if (sg.getGroupName().equals(AMI_CUSTOMIZATION_SG_NAME)
							&& sg.getDescription().equals(AMI_CUSTOMIZATION_SG_DESCRIPTION)) {
						sgExist = true;
						success = new Boolean(true);
					}
				}
			}

			if (!sgExist) {
				CreateSecurityGroupRequest createSecurityGroupRequest = new CreateSecurityGroupRequest(
						AMI_CUSTOMIZATION_SG_NAME, AMI_CUSTOMIZATION_SG_DESCRIPTION);
				CreateSecurityGroupResult creationResult = ec2.createSecurityGroup(createSecurityGroupRequest);
				if (creationResult.getGroupId() != null) {
					System.out.println(creationResult);
					success = new Boolean(true);
				}

			}
		} finally {
			if (ec2 != null)
				ec2.shutdown();
		}

		return success;

	}
}
