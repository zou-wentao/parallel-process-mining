package cn.edu.nju.software.parallel;

import java.io.InputStream;

import org.processmining.contexts.uitopia.annotations.UIImportPlugin;
import org.processmining.framework.abstractplugins.AbstractImportPlugin;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;

import cn.edu.nju.software.parallel.algorithm.DecomposeEventLog;


//�������
@Plugin(name = "Import Trace Log", parameterLabels = { "Filename" }, 
returnLabels = { "TraceSet" }, returnTypes = { DecomposeEventLog.class })
//����������
@UIImportPlugin(
//����������ļ��ᱻת��ΪTraceSet������ProM�н��д���
description = "Trace Log",
//����ʶ����Զ����ļ���׺��Ϊtrace
extensions = { "mxml" })

public class TraceImport extends AbstractImportPlugin{	
	@Override
	protected DecomposeEventLog importFromStream(PluginContext context, InputStream input, String filename, long fileSizeInBytes){
//		// ���ļ�����һ�𷵻�
//        int count = 0;
//		try {
//			BufferedReader br =  new BufferedReader(new InputStreamReader(input));  
//			while (br.readLine()!= null) {  
//				 count ++;
//			}
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
		
//		DecomposeEventLog traceSet = new DecomposeEventLog(input);
//		//traceSet = GenerateTraceSet.generateTraceSet(input);
//		try{
//			context.getFutureResult(0).setLabel("Trace file imported from " + filename);
//			return traceSet;
//		}catch(Exception e){
//			// Don't care if this fails
//			System.out.println("shit!");
//			e.printStackTrace();
			return null;
//		}
	}
	
}