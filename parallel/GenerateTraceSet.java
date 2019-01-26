package cn.edu.nju.software.parallel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import cn.edu.nju.software.parallel.datastructure.Trace;
import cn.edu.nju.software.parallel.datastructure.TraceSet;


public class GenerateTraceSet {
	//��TImport��������������ת��Ϊһ��traceSet��
	public static TraceSet generateTraceSet(InputStream input){
		TraceSet traceSet = new TraceSet();
		long executeTime = 0;
		try{
			BufferedReader br = new BufferedReader(new InputStreamReader(input, "utf-8"));
			String line = "";
			// ��ȡ��Ҫ���еĸ���
			int parts  = Integer.parseInt(br.readLine());
			// ��ȡ�ļ�����
			int lineNums  = Integer.parseInt(br.readLine());
			long startTime = System.nanoTime();
			int diviFlag = lineNums/parts;
			int count = 1;
			// Ȼ����Զ��̲߳�����ȡ�ļ� �˴�Ϊ�˷��� �͵��̴߳���
			while ((line = br.readLine()) != null) {
				count ++;
				if(count > diviFlag){
					long endTime = System.nanoTime();
					executeTime = Math.max(executeTime, (endTime - startTime)/100);
					count = 1;
					startTime = System.nanoTime();
				}
				processLine(traceSet, line);
			}
			long endTime = System.nanoTime();
			executeTime = Math.max(executeTime, (endTime - startTime)/100);
			traceSet.setReadFileTime(executeTime);
			return traceSet;
		}catch(IOException e){
			//do nothing
			return null;
		}
		
	}
	
	//����input����ÿһ�У���ת��Ϊtrace���traceSet��
	private static void processLine(TraceSet traceSet, String line){
		Trace trace = new Trace(line);
		traceSet.addTrace(trace);
	}
}
