/* Copyright 2009-2016 David Hadka
*
* This file is part of the MOEA Framework.
*
* The MOEA Framework is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as published by
* the Free Software Foundation, either version 3 of the License, or (at your
* option) any later version.
*
* The MOEA Framework is distributed in the hope that it will be useful, but
* WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
* or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
* License for more details.
*
* You should have received a copy of the GNU Lesser General Public License
* along with the MOEA Framework.  If not, see <http://www.gnu.org/licenses/>.
*/
package ai.libs.jaicore.ea.algorithm.moea.moeaframework;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.moeaframework.core.EpsilonBoxDominanceArchive;
import org.moeaframework.core.EpsilonBoxEvolutionaryAlgorithm;
import org.moeaframework.core.Initialization;
import org.moeaframework.core.NondominatedSortingPopulation;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Population;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Selection;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variation;
import org.moeaframework.core.comparator.ChainedComparator;
import org.moeaframework.core.comparator.CrowdingComparator;
import org.moeaframework.core.comparator.DominanceComparator;
import org.moeaframework.core.comparator.ParetoDominanceComparator;
import org.moeaframework.core.operator.TournamentSelection;

/**
* Implementation of NSGA-II, with the ability to attach an optional 
* &epsilon;-dominance archive.
* <p>
* References:
* <ol>
*   <li>Deb, K. et al.  "A Fast Elitist Multi-Objective Genetic Algorithm:
*       NSGA-II."  IEEE Transactions on Evolutionary Computation, 6:182-197, 
*       2000.
*   <li>Kollat, J. B., and Reed, P. M.  "Comparison of Multi-Objective 
*       Evolutionary Algorithms for Long-Term Monitoring Design."  Advances in
*       Water Resources, 29(6):792-807, 2006.
* </ol>
*/
public class NSGAII extends AbstractEvolutionaryAlgorithm implements EpsilonBoxEvolutionaryAlgorithm {

	/**
	 * The selection operator.  If {@code null}, this algorithm uses binary
	 * tournament selection without replacement, replicating the behavior of the
	 * original NSGA-II implementation.
	 */
	private final Selection selection;

	/**
	 * The variation operator.
	 */
	private final Variation variation;

	/**
	 * Constructs the NSGA-II algorithm with the specified components.
	 * 
	 * @param problem the problem being solved
	 * @param population the population used to store solutions
	 * @param archive the archive used to store the result; can be {@code null}
	 * @param selection the selection operator
	 * @param variation the variation operator
	 * @param initialization the initialization method
	 */
	public NSGAII(final Problem problem, final NondominatedSortingPopulation population, final EpsilonBoxDominanceArchive archive, final Selection selection, final Variation variation, final Initialization initialization) {
		super(problem, population, archive, initialization);
		this.selection = selection;
		this.variation = variation;
	}

	@Override
	public void iterate() {
		NondominatedSortingPopulation population = this.getPopulation();
		EpsilonBoxDominanceArchive archive = this.getArchive();
		Population offspring = new Population();
		int populationSize = population.size();

		if (this.selection == null) {
			// recreate the original NSGA-II implementation using binary
			// tournament selection without replacement; this version works by
			// maintaining a pool of candidate parents.
			LinkedList<Solution> pool = new LinkedList<Solution>();

			DominanceComparator comparator = new ChainedComparator(new ParetoDominanceComparator(), new CrowdingComparator());

			while (offspring.size() < populationSize) {
				// ensure the pool has enough solutions
				while (pool.size() < 2 * this.variation.getArity()) {
					List<Solution> poolAdditions = new ArrayList<Solution>();

					for (Solution solution : population) {
						poolAdditions.add(solution);
					}

					PRNG.shuffle(poolAdditions);
					pool.addAll(poolAdditions);
				}

				// select the parents using a binary tournament
				Solution[] parents = new Solution[this.variation.getArity()];

				for (int i = 0; i < parents.length; i++) {
					parents[i] = TournamentSelection.binaryTournament(pool.removeFirst(), pool.removeFirst(), comparator);
				}

				// evolve the children
				offspring.addAll(this.variation.evolve(parents));
			}
		} else {
			// run NSGA-II using selection with replacement; this version allows
			// using custom selection operators
			while (offspring.size() < populationSize) {
				Solution[] parents = this.selection.select(this.variation.getArity(), population);
				offspring.addAll(this.variation.evolve(parents));
			}
		}

		this.evaluateAll(offspring);

		if (archive != null) {
			archive.addAll(offspring);
		}

		population.addAll(offspring);
		population.truncate(populationSize);
	}

	@Override
	public EpsilonBoxDominanceArchive getArchive() {
		return (EpsilonBoxDominanceArchive) super.getArchive();
	}

	@Override
	public NondominatedSortingPopulation getPopulation() {
		return (NondominatedSortingPopulation) super.getPopulation();
	}

}
