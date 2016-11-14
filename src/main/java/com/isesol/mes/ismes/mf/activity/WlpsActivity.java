package com.isesol.mes.ismes.mf.activity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.isesol.ismes.platform.core.service.bean.Dataset;
import com.isesol.ismes.platform.module.Bundle;
import com.isesol.ismes.platform.module.Parameters;
import com.isesol.ismes.platform.module.Sys;
import com.isesol.mes.ismes.mf.constant.CustomConstant;

public class WlpsActivity {
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat sdf_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	/**
	 * 跳转物料配送页面
	 * 
	 * @param parameters
	 * @param bundle
	 * @return
	 */
	public String query_wlps(Parameters parameters, Bundle bundle) {
		return "mf_wlps";
	}
	public String query_ljzb1(Parameters parameters, Bundle bundle) {
		bundle.put("rows", "");
		bundle.put("total", 10);
		bundle.put("page", 1);
		bundle.put("records", 1000);
		return "json:";
	}
	
	/**查询加工零件准备
	 * @param parameters
	 * @param bundle
	 * @return
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	public String query_ljzb(Parameters parameters, Bundle bundle) throws Exception {

		if(null == parameters.getString("query_date"))
		{
			return "json:";
		}
		//处理时间参数开始
		String jhkssj_start = parameters.getString("query_date")+" 00:00:00";
		String jhkssj_end = parameters.getString("query_date")+" 23:59:59";
		parameters.set("jhkssj_start", sdf_time.parse(jhkssj_start));
		parameters.set("jhkssj_end",  sdf_time.parse(jhkssj_end));
		//处理时间参数结束
		
		//查询物料信息
		Bundle b_wlxx = Sys.callModuleService("mm", "mmservice_wlxxBybhmc", parameters);
		if (null== b_wlxx) {
			return "json:";
		}
		List<Map<String, Object>> wlxx = (List<Map<String, Object>>) b_wlxx.get("wlxx");
		String val_wl = "(";
		for (int i = 0; i < wlxx.size(); i++) {
			if(i!=0)
			{
				val_wl = val_wl +",";
			}
			val_wl += wlxx.get(i).get("wlid");
		} 
		val_wl = val_wl +")";
		parameters.set("val_wl", val_wl);
		
		//查询设备信息
		Bundle b_sbxx = Sys.callModuleService("em", "emservice_sbxxbysbwz", parameters);
		if (null== b_sbxx) {
			return "json:";
		}
		List<Map<String, Object>> sbxx = (List<Map<String, Object>>) b_sbxx.get("sbxx");
		String val_sb = "(";
		for (int i = 0; i < sbxx.size(); i++) {
			if(i!=0)
			{
				val_sb = val_sb +",";
			}
			val_sb += sbxx.get(i).get("sbid");
		} 
		val_sb = val_sb +")";
		parameters.set("val_sb", val_sb);
		
		
		//查询工单信息,准备清单信息
		Bundle b_gdxx = Sys.callModuleService("pl", "plservice_gdxx", parameters);
		if (null== b_gdxx) {
			return "json:";
		}
		List<Map<String, Object>> gdxx = (List<Map<String, Object>>) b_gdxx.get("gdxx");
		
		if (gdxx.size()<=0) {
			return "json:";
		}
		
		
		for (int i = 0; i < gdxx.size(); i++) {
			for (int j = 0; j < sbxx.size(); j++) {
				if (gdxx.get(i).get("sbid").toString().equals(sbxx.get(j).get("sbid").toString())) {
					gdxx.get(i).put("sbwz", sbxx.get(j).get("sbwz"));
					break;
				}
			}
		} 
		
		for (int i = 0; i < gdxx.size(); i++) {
			for (int j = 0; j < wlxx.size(); j++) {
				if (gdxx.get(i).get("wlid").toString().equals(wlxx.get(j).get("wlid").toString())) {
					gdxx.get(i).put("wlgg", wlxx.get(j).get("wlgg"));
					gdxx.get(i).put("wlgg", wlxx.get(j).get("wlmc"));
					gdxx.get(i).put("wlgg", wlxx.get(j).get("wlbh"));
					break;
				}
			}
		}
		
//		
//		//查询零件信息
//		Bundle b_ljxx = Sys.callModuleService("pm", "pmservice_ljxx", parameters);
//		List<Map<String, Object>> ljxx = (List<Map<String, Object>>) b_ljxx.get("ljxx");
//		for (int i = 0; i < gdxx.size(); i++) {
//			for (int j = 0; j < ljxx.size(); j++) {
//				if (gdxx.get(i).get("ljid").toString().equals(ljxx.get(j).get("ljid").toString())) {
//					gdxx.get(i).put("ljbh", ljxx.get(j).get("ljbh"));
//					break;
//				}
//			}
//		}
		
		bundle.put("rows", gdxx);
		return "json:";
	}
	
	
	
	/**工装准备信息
	 * @param parameters
	 * @param bundle
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public String table_djzb(Parameters parameters, Bundle bundle) throws Exception {
		if(null == parameters.getString("query_date"))
		{
			parameters.set("query_date", sdf.format(new Date()));
		}
		String query_ydwsjstart=StringUtils.isBlank(parameters.getString("query_ydwsjstart"))?" 00:00:00":" "+parameters.get("query_ydwsjstart").toString();
		String query_ydwsjend=StringUtils.isBlank(parameters.getString("query_ydwsjend"))?" 23:59:59":" "+parameters.get("query_ydwsjend").toString();
		
		//处理时间参数开始
		String jhkssj_start = parameters.getString("query_date")+query_ydwsjstart;
		String jhkssj_end = parameters.getString("query_date")+query_ydwsjend;
		parameters.set("jhkssj_start", sdf_time.parse(jhkssj_start));
		parameters.set("jhkssj_end",  sdf_time.parse(jhkssj_end));
		//处理时间参数结束
		
		//查询物料信息
		Bundle b_wlxx = Sys.callModuleService("mm", "mmservice_wlxxBybhmc", parameters);
		if (null== b_wlxx) {
			return "json:";
		}
		List<Map<String, Object>> wlxx = (List<Map<String, Object>>) b_wlxx.get("wlxx");
		if (null== wlxx || wlxx.size() == 0) {
			return "json:";
		}
		
		String val_wl = "(";
		for (int i = 0; i < wlxx.size(); i++) {
			if(i!=0)
			{
				val_wl = val_wl +",";
			}
			val_wl += wlxx.get(i).get("wlid");
		} 
		val_wl = val_wl +")";
		parameters.set("val_wl", val_wl);
		
		//查询毛坯炉号
		Bundle b_mplh = Sys.callModuleService("wm", "wm_query_mplh", parameters);
		List<Map<String, Object>> mplhxx = null;
		if(b_mplh != null && b_mplh.get("mplh") != null){
			mplhxx = (List<Map<String, Object>>) b_mplh.get("mplh");
		}
		
		//查询设备信息
		Bundle b_sbxx = Sys.callModuleService("em", "emservice_qjgdyandsbxxbysbwz", parameters);
		if (null== b_sbxx) {
			return "json:";
		}
		List<Map<String, Object>> sbxx = (List<Map<String, Object>>) b_sbxx.get("jgdyandsbxx");
		if (null== sbxx || sbxx.size() == 0) {
			return "json:";
		}
		String val_sb = "(";
		for (int i = 0; i < sbxx.size(); i++) {
			if(i!=0)
			{
				val_sb = val_sb +",";
			}
			val_sb += sbxx.get(i).get("jgdyid");
		} 
		val_sb = val_sb +")";
		parameters.set("val_sb", val_sb);
		
		
		//查询工单信息,准备清单信息
		Bundle b_gdxx = Sys.callModuleService("pl", "plservice_gdxx", parameters);
		if (null== b_gdxx) {
			return "json:";
		}
		List<Map<String, Object>> gdxx = (List<Map<String, Object>>) b_gdxx.get("gdxx");
		
		if (gdxx.size()<=0) {
			return "json:";
		}
		
		String val_gd = "(";
		for (int i = 0; i < gdxx.size(); i++) {
			if(i!=0)
			{
				val_gd = val_gd +",";
			}
			val_gd += gdxx.get(i).get("pcid");
		}
		val_gd = val_gd +")";
		parameters.set("val_gd", val_gd);
		//获取生产编号
		Bundle dataset_scbh = Sys.callModuleService("pro", "proService_getScph", parameters);
		
		List<Map<String, Object>> scbh = null;
		
		if(dataset_scbh != null && dataset_scbh.get("scbh") != null){
			scbh = (List<Map<String, Object>>) dataset_scbh.get("scbh");
		}
		
		//添加毛坯炉号
		if(mplhxx != null && mplhxx.size() > 0){
			for (int i = 0; i < gdxx.size(); i++) {
				for (int j = 0; j < mplhxx.size(); j++) {
					if (gdxx.get(i).get("wlid").toString().equals(mplhxx.get(j).get("wlid").toString())) {
						gdxx.get(i).put("mplh", mplhxx.get(j).get("mplh"));
						break;
					}
				}
			}
		}
		
		//添加生产编号
		if(scbh != null && scbh.size() > 0){
			for (int i = 0; i < gdxx.size(); i++) {
				for (int j = 0; j < scbh.size(); j++) {
					if (gdxx.get(i).get("pcid").toString().equals(scbh.get(j).get("scrwpcid").toString())) {
						gdxx.get(i).put("scrwbh", scbh.get(j).get("scph"));
						gdxx.get(i).put("mplh", scbh.get(j).get("mplh"));
						break;
					}
				}
			}
		}
		
		
		for (int i = 0; i < gdxx.size(); i++) {
			for (int j = 0; j < sbxx.size(); j++) {
				if (gdxx.get(i).get("sbid").toString().equals(sbxx.get(j).get("jgdyid").toString())) {
					gdxx.get(i).put("sbwz", sbxx.get(j).get("sbwz"));
					break;
				}
			}
		} 
		
		for (int i = 0; i < gdxx.size(); i++) {
			for (int j = 0; j < wlxx.size(); j++) {
				if (gdxx.get(i).get("wlid").toString().equals(wlxx.get(j).get("wlid").toString())) {
					gdxx.get(i).put("wlgg", wlxx.get(j).get("wlgg"));
					gdxx.get(i).put("wlmc", wlxx.get(j).get("wlmc"));
					gdxx.get(i).put("wlbh", wlxx.get(j).get("wlbh"));
					gdxx.get(i).put("wltm", wlxx.get(j).get("wltm"));
					break;
				}
			}
		}
		
				
		
		bundle.put("rows", gdxx);
		return "json:";
	}
	
	/**
	 * 保存物料送达信息
	 * 
	 * @param parameters
	 * @param bundle
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public void saveWlsd(Parameters parameters, Bundle bundle) {
		String gdbh = (String) parameters.get("gdbh");
		String wlbh = (String) parameters.get("wlbh");
		int sl = Integer.parseInt(parameters.getString("sl"));
		String sbbh = (String) parameters.get("sbbh");
		String wllb = (String) parameters.get("wllb");
		String flag = (String) parameters.get("flag");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(CustomConstant.物料送达接收表_工单编号, gdbh);
		map.put(CustomConstant.物料送达接收表_设备编号, sbbh);
		map.put(CustomConstant.物料送达接收表_物料编号, wlbh);
		map.put(CustomConstant.物料送达接收表_物料类别, wllb);
		int count = 0;
		if("sd".equals(flag)){
			map.put(CustomConstant.物料送达接收表_送达者, Sys.getUserIdentifier());
			map.put(CustomConstant.物料送达接收表_送达时间, new Date());
			Dataset dataset = Sys.query(CustomConstant.物料送达接收表, CustomConstant.物料送达接收表_送达数量,
					" gdbh = ? and sbbh = ? and wlbh = ? and wllb= ? ", null, new Object[] { gdbh, sbbh, wlbh,wllb });
			if (null != dataset && dataset.getList().size() > 0) {
				int sdsl_sql = 0;
				if(null != dataset.getMap().get(CustomConstant.物料送达接收表_送达数量)){
					sdsl_sql=Integer.parseInt(dataset.getMap().get(CustomConstant.物料送达接收表_送达数量).toString());
				}
				map.put(CustomConstant.物料送达接收表_送达数量,
						sl + sdsl_sql);
				count = Sys.update(CustomConstant.物料送达接收表, map, " gdbh = ? and sbbh = ? and wlbh = ? and wllb = ?",
						new Object[] { gdbh, sbbh, wlbh, wllb });
			} else {
				map.put(CustomConstant.物料送达接收表_送达数量, sl);
				count = Sys.insert(CustomConstant.物料送达接收表, map);
			}
		} else {
			map.put(CustomConstant.物料送达接收表_接收者, Sys.getUserIdentifier());
			map.put(CustomConstant.物料送达接收表_接收时间, new Date());
			Dataset dataset = Sys.query(CustomConstant.物料送达接收表, CustomConstant.物料送达接收表_接收数量,
					" gdbh = ? and sbbh = ? and wlbh = ? and wllb= ? ", null, new Object[] { gdbh, sbbh, wlbh,wllb });
			if (null != dataset && dataset.getList().size() > 0) {
				int jssl_sql = 0;
				if(null != dataset.getMap().get(CustomConstant.物料送达接收表_接收数量)){
					jssl_sql=Integer.parseInt(dataset.getMap().get(CustomConstant.物料送达接收表_接收数量).toString());
				}
				map.put(CustomConstant.物料送达接收表_接收数量,
						sl + jssl_sql);
				count = Sys.update(CustomConstant.物料送达接收表, map, " gdbh = ? and sbbh = ? and wlbh = ? and wllb = ?",
						new Object[] { gdbh, sbbh, wlbh, wllb });
			} else {
				map.put(CustomConstant.物料送达接收表_接收数量, sl);
				count = Sys.insert(CustomConstant.物料送达接收表, map);
			}
		}
		bundle.put("count", count);
	}
	
	
	
	/**
	 * 保存物料送达信息
	 * 
	 * @param parameters
	 * @param bundle
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public void saveGzsd(Parameters parameters, Bundle bundle) {
		String sdtm = (String) parameters.get("sdtm");
		String wllb = (String) parameters.get("wllb");
		String gzbh = (String) parameters.get("gztm");
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(CustomConstant.工装送达接收表_工装编号, gzbh);
		map.put(CustomConstant.工装送达接收表_设备编号, sdtm);
		map.put(CustomConstant.工装送达接收表_物料类别, wllb);
		map.put(CustomConstant.工装送达接收表_送达时间, new Date());
		map.put(CustomConstant.工装送达接收表_送达者, Sys.getUserIdentifier());
		map.put("wltm", (String)parameters.get("wltm"));
		
		int count = Sys.insert(CustomConstant.工装送达接收表, map);

		bundle.put("count", count);
	}
	
	
	
	
	/**
	 * 
	 * @param parameters
	 * @param bundle
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public void querySl(Parameters parameters, Bundle bundle) {
		String gdbh = (String) parameters.get("gdbh");
		String wlbh = (String) parameters.get("wlbh");
		String sbbh = (String) parameters.get("sbbh");
		String wllb = (String) parameters.get("wllb");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(CustomConstant.物料送达接收表_工单编号, gdbh);
		map.put(CustomConstant.物料送达接收表_设备编号, sbbh);
		map.put(CustomConstant.物料送达接收表_物料编号, wlbh);
		map.put(CustomConstant.物料送达接收表_物料类别, wllb);
		Dataset dataset = Sys.query(CustomConstant.物料送达接收表, "sdsl,jssl",
				" gdbh = ? and sbbh = ? and wlbh = ? and wllb = ?", null, new Object[] { gdbh, sbbh, wlbh,wllb });
		int count = 0;
		if (null != dataset && dataset.getList().size() > 0) {
			int sdsl_sql = 0;
			int jssl_sql = 0;
			if(null != dataset.getMap().get(CustomConstant.物料送达接收表_送达数量)){
				sdsl_sql=Integer.parseInt(dataset.getMap().get(CustomConstant.物料送达接收表_送达数量).toString());
			}
			if(null != dataset.getMap().get(CustomConstant.物料送达接收表_接收数量)){
				jssl_sql=Integer.parseInt(dataset.getMap().get(CustomConstant.物料送达接收表_接收数量).toString());
			}
			count = sdsl_sql-jssl_sql;
		} 
		bundle.put("count", count);
	}
	
	/**
	 * 
	 * @param parameters
	 * @param bundle
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public void queryMaxZhsdsj(Parameters parameters, Bundle bundle) {
		String gdbh = (String) parameters.get("gdbh");
		String wlbh = (String) parameters.get("wlbh");
		String sbbh = (String) parameters.get("sbbh");
		String wllb = (String) parameters.get("wllb");
		String lxqf = "10";
		
		Dataset dataset = Sys.query(CustomConstant.物料送达接收流水表, "max(xtdqsj) zhsdsj",
				" gdbh = ? and sbbh = ? and wlbh = ? and wllb = ? and lxqf = ? ", null, new Object[] { gdbh, sbbh, wlbh,wllb,lxqf });
		if (null != dataset && dataset.getList().size() > 0) {
			if(null == dataset.getMap().get("zhsdsj")){
				bundle.put("zhsdsj", "");
			} else {
				bundle.put("zhsdsj", dataset.getMap().get("zhsdsj"));
			}
		} else {
			bundle.put("zhsdsj", "");
		}
		
	}

	
	/**
	 * 
	 * @param parameters
	 * @param bundle
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public void queryMaxGzzhxx(Parameters parameters, Bundle bundle) {
		
		String sdtm = (String) parameters.get("sdtm");
		String wllb = (String) parameters.get("wllb");
		String gzbh = (String) parameters.get("gztm");
		Dataset dataset = Sys.query(CustomConstant.工装送达接收表, "max(sdsj) zhsdsj",
				" sbbh = ? and wllb = ? and gzbh = ? ", null, new Object[] { sdtm, wllb, gzbh });
		if (null != dataset && dataset.getList().size() > 0) {
			bundle.put("zhsdsj", dataset.getMap().get("zhsdsj"));
		} else {
			bundle.put("zhsdsj", "");
		}
		
	}
	
	
	/**
	 * 保存物料送达流水信息
	 * 
	 * @param parameters
	 * @param bundle
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public void saveWlsdls(Parameters parameters, Bundle bundle) {
		String gdbh = (String) parameters.get("gdbh");
		String wlbh = (String) parameters.get("wlbh");
		int sl = Integer.parseInt(parameters.getString("sl"));
		String sbbh = (String) parameters.get("sbbh");
		String wllb = (String) parameters.get("wllb");
		String flag = (String) parameters.get("flag");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(CustomConstant.物料送达接收流水表_设备编号, sbbh);
		map.put(CustomConstant.物料送达接收流水表_工单编号, gdbh);
		map.put(CustomConstant.物料送达接收流水表_物料编号, wlbh);
		map.put(CustomConstant.物料送达接收流水表_物料类别, wllb);
		map.put(CustomConstant.物料送达接收流水表_操作人, Sys.getUserIdentifier());
		if("sd".equals(flag)){
			map.put(CustomConstant.物料送达接收流水表_类型区分, CustomConstant.物料送达接收流水表_类型区分_送达);
		}else{
			map.put(CustomConstant.物料送达接收流水表_类型区分, CustomConstant.物料送达接收流水表_类型区分_接收);
		}
		
		map.put(CustomConstant.物料送达接收流水表_数量, sl);
		map.put(CustomConstant.物料送达接收流水表_操作时间, new Date());
		int count = Sys.insert(CustomConstant.物料送达接收流水表, map);

		bundle.put("count", count);
	}

	/**
	 * 保存物料送达信息
	 * 
	 * @param parameters
	 * @param bundle
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public void saveGzjs(Parameters parameters, Bundle bundle) {
		
		String sdtm = (String) parameters.get("sdtm");
		String wllb = (String) parameters.get("wllb");
		String gzbh = (String) parameters.get("gztm");
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(CustomConstant.工装送达接收表_工装编号, gzbh);
		map.put(CustomConstant.工装送达接收表_设备编号, sdtm);
		map.put(CustomConstant.工装送达接收表_物料类别, wllb);
		map.put(CustomConstant.工装送达接收表_接收时间, new Date());
		map.put(CustomConstant.工装送达接收表_接收者, Sys.getUserIdentifier());
		int count = 0;
		count = Sys.insert(CustomConstant.工装送达接收表, map);
		bundle.put("count", count);
	}

	/**
	 * 保存物料送达流水信息
	 * 
	 * @param parameters
	 * @param bundle
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public void saveGzjsls(Parameters parameters, Bundle bundle) {
		String gdbh = (String) parameters.get("gdbh");
		String wlbh = (String) parameters.get("wlbh");
		int jssl = Integer.parseInt(parameters.getString("jssl"));
		String sbbh = (String) parameters.get("sbbh");
		String wllb = (String) parameters.get("wllb");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(CustomConstant.物料送达接收流水表_设备编号, sbbh);
		map.put(CustomConstant.物料送达接收流水表_工单编号, gdbh);
		map.put(CustomConstant.物料送达接收流水表_物料编号, wlbh);
		map.put(CustomConstant.物料送达接收流水表_物料类别, wllb);
		map.put(CustomConstant.物料送达接收流水表_类型区分, CustomConstant.物料送达接收流水表_类型区分_接收);
		map.put(CustomConstant.物料送达接收流水表_数量, jssl);
		map.put(CustomConstant.物料送达接收流水表_操作时间, new Date());
		int count = Sys.insert(CustomConstant.物料送达接收流水表, map);
		bundle.put("count", count);
	}
}
