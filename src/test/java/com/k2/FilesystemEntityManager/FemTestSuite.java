package com.k2.FilesystemEntityManager;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	FemConfigTest.class,
	FemSingleActionTest.class,
	FemMultipleActionTest.class,
	FemSingleActionXmlTest.class,
	FemMultipleActionXmlTest.class,
	FemMultiThreadedTest.class,
	FemRawTest.class,
	KeyUtilTest.class,
	LinkTest.class,
	MetamodelTest.class,
	EntityUtilTest.class,
	FemQueryTest.class
})
public class FemTestSuite {

}
