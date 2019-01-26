package cn.edu.nju.software.parallel.algorithm;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import cn.edu.nju.software.parallel.datastructure.JavaUtils;

public class MineSubModel {
	// �ھ��ӽṹ �����Ӧ���ӽṹ�����е�loop��Ϣ
	private HashMap<String, ArrayList<String>> submodel_subloop;
	// �����ھ���ģ��ʱ��
	private long mineModelTime = 0;
	// ����ظ���Ϣʱ��
	private long clearModelTime = 0;
	
	public long getMineModelTime() {
		return mineModelTime;
	}

	public void setMineModelTime(long mineModelTime) {
		this.mineModelTime = mineModelTime;
	}

	public long getClearModelTime() {
		return clearModelTime;
	}

	public void setClearModelTime(long clearModelTime) {
		this.clearModelTime = clearModelTime;
	}
	
	

	public MineSubModel(Set<Entry<String, ArrayList<String>>> sng, DecomposeEventLog del) {
		submodel_subloop = new HashMap<String, ArrayList<String>>();
		// sng�е�Ԫ�ؽ�����traceset�е����˳�� ������trace�е����˳�� �� sng��keyֵ�������
		for(Entry<String, ArrayList<String>> pairs : sng) {
			long startTime = System.nanoTime();
			// ÿѭ��һ�� ��һЩѭ���ṹ��ӽ�model��
			//List<String> subModel = JavaUtils.string2list(del.getsTaskSet().get(pairs.getKey()).get(0), ",");
			List<String> subModel = JavaUtils.string2list(pairs.getKey(), ",");
			//  loop_tasks ������ѭ����·�������е�tasks�ļ���
			for(String loop_tasks : pairs.getValue()){
				// �������loop�ĳ��ں����
				// ���ض�Ӧ��taskSet�е�traces
				List<String> traces = del.getsTaskSet().get(loop_tasks);
				// ��Ӧfirstng��submodel��list��ʽ
				List<String> tasks = JavaUtils.string2list(loop_tasks, ",");
				tasks.removeAll(subModel);
				String loop = JavaUtils.list2string(tasks, ",");
				if(loop != "" && loop != null) {
					// ѭ������֮ǰ�ظ����ֵĵ�һ���ǳ��ڣ����һ�������
					String starterAndEnder = findStartandEnd(traces, tasks);
					//System.out.println(starterAndEnder);
					//System.out.println("----");
					if(!starterAndEnder.equals(",")) {
						//System.out.println(starterAndEnder);
						//System.out.println("*****");
						String starter = starterAndEnder.split(",")[0];
						String ender = starterAndEnder.split(",")[1];
						
						loop = starter + "," + loop +  "," + ender;
					}

					for(String str : tasks) {
						subModel.add(str);
					}
					// ��� ȡ��һ��Ԫ��������Ļ� �� ���� list Ѱ�����е�Ԫ��ֻ����һ�ε����
					List<String> subModelTraces = del.getsTaskSet().get(pairs.getKey());					
					List<String> subModelList = JavaUtils.string2list(findSubModel(subModelTraces), ",");					
					String subModStr = JavaUtils.list2string(subModelList, ",");
					if(submodel_subloop.isEmpty()) {
						ArrayList<String> subloops = new ArrayList<String>();
						subloops.add(loop);
						submodel_subloop.put(subModStr, subloops);
					}
					else {
						if(submodel_subloop.containsKey(subModStr)) {
							submodel_subloop.get(subModStr).add(loop);
						} else {
							ArrayList<String> subloops = new ArrayList<String>();
							subloops.add(loop);
							submodel_subloop.put(subModStr, subloops);
						}
					}
				}
			}
			long endTime = System.currentTimeMillis();
			long total = endTime - startTime;
			mineModelTime = Math.max(mineModelTime, total);
		}
		long startTime = System.currentTimeMillis();
		clearLoop(submodel_subloop);
		long endTime = System.currentTimeMillis();
		long total = endTime - startTime;
		clearModelTime = total;
	}
	
	private String findSubModel(List<String> subModelTraces) {
		return subModelTraces.get(0);
		
	}

	// ������ɾ���loop ��ǰ��� �ų�special group ����
	// ʹ��û��ѭ����������֧��
	private void clearLoop(HashMap<String, ArrayList<String>> submodel) {
		HashMap<String, ArrayList<String>> copySng = clone(submodel);
		for(Entry<String, ArrayList<String>> pairs : copySng.entrySet()) {
			for(String str : pairs.getValue()) {
				for(String firstng : copySng.keySet()) {
					if(firstng != pairs.getKey()) {
						List<String> fngList =  JavaUtils.string2list(firstng, ",");
						fngList.removeAll(JavaUtils.string2list(pairs.getKey(), ","));
						List<String> tempList = JavaUtils.string2list(str, ",");
						for(String task : fngList) {
							if(tempList.contains(task)) {
								submodel.get(pairs.getKey()).remove(str);
								break;
							}
						}
					}
				}
			}
		}
	}
	
	// ���л������������
	@SuppressWarnings("unchecked")
	private <T extends Serializable> T clone(T obj) {
		T clonedObj = null;
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(obj);
			oos.close();
			ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
			ObjectInputStream ois = new ObjectInputStream(bais);
			clonedObj = (T) ois.readObject();
			ois.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return clonedObj;
	}
	
	
	
	// �ҳ�ѭ���ṹ�ĳ���
	// trace���ǰ������ѭ�������е�·��   tasks��ѭ���ڲ��ṹ
	// ��Ҫ���ѭ��Ƕ�׵�����
	private String findStartandEnd(List<String> traces, List<String> tasks) {
		String starter = "";
		String ender = "";
		List<String> freTasks = new ArrayList<String>();
		// flag1 ������ʶ �Ƿ����ѭ��
		// flag2 ������ʶ �Ƿ��� start
		int flag1 = 0, flag2 = 0;
		// ��ʱֻ��ÿ��traces�еĵ�һ��trace
		// Ӧ����ʹ����ÿ��Ԫ�ض�����һ�ε�trace
		
//		for(String str : tasks) {
//			System.out.println(str);
//		}
//		System.out.println("&&&&&&&");
		
		List<String> traceList = JavaUtils.string2list(traces.get(0), ",");
		for(String task : traceList) {
			//System.out.println(task);
			if(!tasks.contains(task)) {
				if(flag1 == 0) {
					freTasks.add(task);
				} else if (freTasks.contains(task)){
					if(flag2 == 0) {
						ender = task;
						flag2 = 1;
					} else {
						starter = task;
					}
				}
			} else {
				flag1 = 1;
			}
		}
		return starter+","+ender;
	}
	
	public HashMap<String, ArrayList<String>> getSubmodel_subloop() {
		return submodel_subloop;
	}
	public void setSubmodel_subloop(HashMap<String, ArrayList<String>> submodel_subloop) {
		this.submodel_subloop = submodel_subloop;
	}
}