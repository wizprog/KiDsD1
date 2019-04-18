package components;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import exceptions.NonExistingCommand;
import interfaces.ResultInterface;

public class ResultRetrieverThreadPool implements Runnable, ResultInterface {

	ExecutorService ex;
	private Map<String, Map<String, Integer>> webResultData; // cache
	private Map<String, Map<String, Integer>> fileResultData; // cache
	Semaphore putResultSemaphore, summarySemaphore, resultSemaphore;

	public ResultRetrieverThreadPool() {
		super();
		webResultData = new HashMap<String, Map<String, Integer>>();
		fileResultData = new HashMap<String, Map<String, Integer>>();
		// !!!!!!!!!!!!!!!!!!!!!!!!! verovatno se inicijalizuju sa 1,0,0 ali izguglaj
		putResultSemaphore = new Semaphore(0); // treba samo 1 nit da prolazi
		summarySemaphore = new Semaphore(-1); // treba da se odmah zablokira
		resultSemaphore = new Semaphore(-1); // treba da se odmah zablokira
	}

	@Override
	public void run() {
		ex = Executors.newFixedThreadPool(10);

		System.out.println("Web Scanner Thread Pool started...");
		while (true) {
			try {

			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		}
	}

	public void putResult(Map<String, Integer> result, String type, String key) {
		try {

			// !!!!!!!!!!!
			// https://stackoverflow.com/questions/25563640/difference-between-semaphore-initialized-with-1-and-0
			// mozda treba da se inicijalizuju na 1, da bi prvi put prosao, ovo ti koristis
			// kao region tj da samo 1 nit upisuje
			// sto znaci da semafor treba da ima vrednost 1 jer je to broj permitova,
			// sumarry i result trebaju da su na 0 jer
			// se zablokiraju dokle god druga nit im ne da release da prodju

			putResultSemaphore.acquire();
			if (type.equals("WEB")) {
				webResultData.put(key, result);
			} else {
				fileResultData.put(key, result);
			}
			putResultSemaphore.release();

			// moze i ovako jer se ovako definisu regioni u javi
			// synchronized (this) {
			// if (type.equals("WEB")) {
			// webResultData.put(key, result);
			// } else {
			// fileResultData.put(key, result);
			// }
			// }

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void stop() {
		ex.shutdown();
	}

	public String parseName(String query) {
		String name = query.substring(query.indexOf('|') + 1, query.length() - 1);
		return name;
	}

	public String parseType(String query) {
		String type = query.substring(0, query.indexOf('|'));
		return type;
	}

	@Override
	public Map<String, Integer> getResult(String query) {

		return null;
	}

	@Override
	public Map<String, Integer> queryResult(String query) throws Exception {
		//mozda bi mogli celu logiku za blokiranje da prebacimo u main
		if (parseType(query).equals("web")) {
			/*
			 * dokle god nije ispunjeno jedno od sledeceg: ne postoji korpus ili ima nula
			 * (nisu svi zavrsili) konstantno se zablokiraj na semaforu, razlog je sto je
			 * moguc slucaj da vise threadova nisu zavrsili, pa kad prvi zavrsi ne sme da
			 * probudi ovu metodu dokle god nisu svi zavrsili, kada nema nula i postoji
			 * korpus vrati vrednost
			 */
			while (!webResultData.containsKey(parseName(query))
					|| webResultData.get(parseName(query)).containsValue(0)) {
				resultSemaphore.acquire();

			}
			return webResultData.get(parseName(query));

		} else if (parseType(query).equals("file")) {
			while (!fileResultData.containsKey(parseName(query))
					|| fileResultData.get(parseName(query)).containsValue(0)) {
				resultSemaphore.acquire();
			}
			return fileResultData.get(parseName(query));
		} else {
			throw new NonExistingCommand("Non existing command");
		}
	}

	@Override
	public void clearSummary(String summaryType) {
		if (summaryType.equals("WEB")) {
			webResultData.clear();
		} else {
			fileResultData.clear();
		}
	}

	@Override
	public Map<String, Map<String, Integer>> getSummary(String summaryType) {
		try {
			if (summaryType.equals("web")) {
				if (!isFinished(summaryType)) {
					summarySemaphore.acquire(); // treba da se zablokira jer nije zavrsen summary, cim prodje moze da
												// vrati
					return webResultData;
				}
			} else {
				if (!isFinished(summaryType)) {

					summarySemaphore.acquire(); // treba da se zablokira jer nije zavrsen summary, cim prodje moze da
												// vrati
					return fileResultData;
				}
			}

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Map<String, Map<String, Integer>> querySummary(String summaryType) { // obradjuje se samo slucaj kada je
																				// val==0
		if (summaryType.equals("web")) {
			if (!isFinished(summaryType)) {
				Map<String, Map<String, Integer>> ret = new HashMap<String, Map<String, Integer>>();
				ret.put("NOTFINISHED", new HashMap<String, Integer>());
				return ret;
			} else
				return webResultData;
		} else {
			if (!isFinished(summaryType)) {
				Map<String, Map<String, Integer>> ret = new HashMap<String, Map<String, Integer>>();
				ret.put("NOTFINISHED", new HashMap<String, Integer>());
				return ret;
			} else
				return fileResultData;
		}
	}

	@Override
	public void addCorpusResult(String corpusName, Map<String, Integer> corpusResult) {
		// TODO Auto-generated method stub

	}

	public boolean isFinished(String type) {
		if (type.equals("web")) {
			Iterator it = webResultData.entrySet().iterator();
			while (it.hasNext()) {
				Map<String, Integer> val = (Map<String, Integer>) it.next();
				if (val.containsValue(0)) {
					return false;
				}
			}
			return true;
		} else {
			Iterator it = fileResultData.entrySet().iterator();
			while (it.hasNext()) {
				Map<String, Integer> val = (Map<String, Integer>) it.next();
				if (val.containsValue(0)) {
					return false;
				}
			}
			return true;
		}
	}
	
	public void clearWebResultData() {
		try {
			resultSemaphore.acquire();
			webResultData.clear();
			resultSemaphore.release();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
