package com.k2.FilesystemEntityManager;

import com.google.gson.annotations.Expose;

public class FemOcn {
	@Expose public Integer ocn;
	
	FemOcn() {}
	
	FemOcn(Integer ocn) {
		this.ocn = ocn;
	}
}
