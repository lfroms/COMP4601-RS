package edu.carleton.comp4601.store.graph;

import java.util.Map;
import java.util.stream.Collectors;

import org.jgrapht.Graph;
import org.jgrapht.alg.scoring.PageRank;
import org.jgrapht.graph.DefaultEdge;

import edu.carleton.comp4601.models.Identifiable;

final class PageRanker {
	public static <DocumentType extends Identifiable> Map<Integer, Double> computeRanks(Graph<DocumentType, DefaultEdge> graph) {
		PageRank<DocumentType, DefaultEdge> pageRank = 
				new PageRank<>(
						graph,
						PageRank.DAMPING_FACTOR_DEFAULT,
						PageRank.MAX_ITERATIONS_DEFAULT
					);

		Map<Integer, Double> output = pageRank
				.getScores()
				.entrySet()
				.stream()
				.collect(Collectors.toMap(document -> document.getKey().getId(), Map.Entry::getValue));

		return output;
	}
}
