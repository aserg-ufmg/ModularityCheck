package br.ufmg.dcc.labsoft.aserg.modularitycheck.enhancements.processing.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import data.handler.CarryFileMemory;

import br.ufmg.dcc.labsoft.aserg.modularitycheck.enhancements.properties.util.Properties;

public class ClusterIntersectionLDA {

	static Map<Integer, String[]> topics;

	/****
	 * Verifica se um cluster esta dentro de um topico
	 */
	public static void combineClusters() {
		Properties
				.setClustersPath("C:\\Users\\luciana.lourdes.LISP\\Dropbox\\Testes\\GeronimoUltimoTeste\\Clusters\\");
		String path = "C:\\Users\\luciana.lourdes.LISP\\Dropbox\\Testes\\GeronimoUltimoTeste\\Results\\clusterFinal";
		try {
			readTopics(path);
		} catch (IOException e) {
			e.printStackTrace();
		}

		Map<Integer, String[]> clusters = new HashMap<Integer, String[]>();
		File[] listing = new File(Properties.getClustersPath()).listFiles();
		int clusterId = 0;
		for (File cluster : listing) {
			File[] issues = cluster.listFiles();
			String[] issueId = new String[issues.length];

			for (int i = 0; i < issues.length; i++) {
				issueId[i] = issues[i].getName();
			}

			clusters.put(clusterId, issueId);
			clusterId++;
		}

		int count = 0;
		int maior = 0;
		int tamanho = 0;
		int topicId = 0;

		Iterator<Integer> iterator = clusters.keySet().iterator();
		while (iterator.hasNext()) {
			clusterId = iterator.next();
			maior = 0;
			String[] issueId = clusters.get(clusterId);
			Iterator<Integer> iteratorTopic = clusters.keySet().iterator();
			while (iteratorTopic.hasNext()) {
				int topic = iteratorTopic.next();
				String[] issueTopic = topics.get(topic);
				count = 0;
				for (String issueT : issueTopic) {
					for (String issueCluster : issueId) {
						if (issueT.trim().equals(issueCluster.trim()))
							count++;
					}
				}
				if (maior < count) {
					maior = count;
					tamanho = issueTopic.length;
					topicId = topic;
				}
			}

			if (issueId.length == maior)
				System.out.println("Cluster " + clusterId
						+ "\t Number of cluster issue: " + issueId.length
						+ "\t Topic: " + topicId
						+ "\t Number of topic issues: " + tamanho
						+ "\t Intersection: " + maior + "\t HIT");
			else
				System.out.println("Cluster " + clusterId
						+ "\t Number of cluster issues: " + issueId.length
						+ "\t Topic: " + topicId
						+ "\t Number of topic issues: " + tamanho
						+ "\t Intersection: " + maior);
		}

	}

	private static void readTopics(String path) throws FileNotFoundException,
			IOException {
		topics = new HashMap<Integer, String[]>();
		String[] openedFile = new CarryFileMemory(path).carryCompleteFile();
		int topic = 0;
		for (String line : openedFile) {
			int index = line.indexOf(Properties.COMMA);
			line = line.substring(index + 2);
			String[] issuesId = line.trim().split(Properties.COMMA);
			topics.put(topic, issuesId);
			topic++;
		}
	}
}
