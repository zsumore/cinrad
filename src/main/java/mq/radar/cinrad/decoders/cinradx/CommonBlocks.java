package mq.radar.cinrad.decoders.cinradx;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ucar.unidata.io.RandomAccessFile;

public class CommonBlocks implements ICinradXBuilder {
	/*
	 * NO 1; Generic Header Block is the first block of all product, it is a
	 * general summary block contains the summary information of the following
	 * other blocks. Generic Header Block keeps information like version of
	 * product format and data type. Generic Header Block is described in Table
	 * 2-2.
	 */
	private GenericHeader genericHeader;
	/*
	 * NO 2; Site Configuration Block is the second block of product, it
	 * provides information on radar site. All parameters related to radar site
	 * should be included in this block. Site Code (No. 1 element in block) is
	 * the unique key of radar site, RPG and PUP can use this code to
	 * distinguish different radar sites. Site Configuration Block is described
	 * in Table 2-4.
	 */
	private SiteConfiguration siteConfiguration;
	/*
	 * NO 3; Task Configuration Block provides information on radar scan task.
	 * Task can be PPI, RHI or Volume Coverage Pattern. Task Configuration Block
	 * includes the general parameters or data of a task, for detail parameters
	 * of elevation or azimuth cut, the Cut Configuration Blocks provides more
	 * descriptions in detail. For most tasks, more than 1 Cut is included, in
	 * which case more than 1 Cut Configuration Blocks are followed. Parameter
	 * Cut Number (No. 4 element in block) is the number of cut followed. Task
	 * Configuration Block is described in Table 2-5.
	 */
	private TaskConfiguration taskConfiguration;
	/*
	 * NO 4; Cut Configuration Block provides information of a specified cut in
	 * task. For most tasks, more than 1 cut may be included. The parameter “Cut
	 * Number” in Task Configuration block decides the number of cuts in the
	 * task. When there are many cuts in one task, cut configuration are stored
	 * in disk one by one follow the order of antenna scanning. Cut
	 * Configuration Block is described in Table 2-6.
	 */
	private List<CutConfiguration> cutConfigurations;

	public GenericHeader getGenericHeader() {
		return genericHeader;
	}

	public SiteConfiguration getSiteConfiguration() {
		return siteConfiguration;
	}

	public TaskConfiguration getTaskConfiguration() {
		return taskConfiguration;
	}

	public List<CutConfiguration> getCutConfigurationList() {
		return cutConfigurations;
	}

	@Override
	public String toString() {
		return "CommonBlocks [genericHeader=" + genericHeader + ", siteConfiguration=" + siteConfiguration
				+ ", taskConfiguration=" + taskConfiguration + ", cutConfigurationList=" + cutConfigurations + "]";
	}

	@Override
	public void builder(RandomAccessFile file, long pos) throws IOException {
		if (pos >= 0)
			file.seek(pos);

		genericHeader = new GenericHeader();
		genericHeader.builder(file, 0);

		siteConfiguration = new SiteConfiguration();
		siteConfiguration.builder(file, -1);

		taskConfiguration = new TaskConfiguration();
		taskConfiguration.builder(file, -1);

		cutConfigurations = new ArrayList<>();
		for (int i = 0; i < taskConfiguration.getCutNumber(); i++) {
			CutConfiguration cutConfiguration = new CutConfiguration();
			cutConfiguration.builder(file, -1);
			cutConfigurations.add(cutConfiguration);
		}

	}

}
