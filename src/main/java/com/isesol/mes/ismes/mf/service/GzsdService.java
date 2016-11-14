package com.isesol.mes.ismes.mf.service;


import com.isesol.ismes.platform.core.service.bean.Dataset;
import com.isesol.ismes.platform.module.Bundle;
import com.isesol.ismes.platform.module.Parameters;
import com.isesol.ismes.platform.module.Sys;

public class GzsdService {
//	private SimpleDateFormat sdf_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	/**查询库存全部数据信息
	 * @param parameters
	 * @param bundle
	 * @return
	 */
	public void query_gzxx_by_wltm(Parameters parameters, Bundle bundle) {
		String param = " 1=1 ";
		if(null!=parameters.get("jsz")&&!"".equals(parameters.get("jsz"))){
			param+=" and jsz like '%"+parameters.get("jsz")+"%'";
		}
		if(null!=parameters.get("jssjstart")&&!"".equals(parameters.get("jssjstart"))){
			param+=" and jssj >'"+parameters.get("jssjstart")+"'";
		}
		if(null!=parameters.get("jssjend")&&!"".equals(parameters.get("jssjend"))){
			param+=" and jssj <'"+parameters.get("jssjend")+"'";
		}
		if(null!=parameters.get("val_tm")&&!"".equals(parameters.get("val_tm"))){
			param+=" and wltm in "+parameters.get("val_tm");
		}
		Dataset dataset_gzxx = Sys.query("mf_gzsdjs"," glid, sbbh, jsz, jssj, sdz, gzbh, wltm, wllb, sdsj ", param, null, new Object[]{});
		bundle.put("gzxx", dataset_gzxx.getList());
	}
	/**查询工装信息通过设备编号
	 * @param parameters
	 * @param bundle
	 * @return
	 */
	public void query_gzxx_by_sbbh(Parameters parameters, Bundle bundle) {
		String param = " 1=1 ";
		if(null!=parameters.get("val_sbbh")&&!"".equals(parameters.get("val_sbbh"))){
			param+=" and sbbh in "+parameters.get("val_sbbh");
		}
		if(null!=parameters.get("val_tm")&&!"".equals(parameters.get("val_tm"))){
			param+=" and wltm in "+parameters.get("val_tm");
		}
		Dataset dataset_gzxx = Sys.query("mf_gzsdjs"," glid, sbbh, jsz, jssj, sdz, gzbh, wltm, wllb, sdsj ", param, null, new Object[]{});
		bundle.put("gzxx", dataset_gzxx.getList());
	}
}
