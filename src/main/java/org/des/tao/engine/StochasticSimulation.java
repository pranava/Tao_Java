package org.des.tao.engine;

import org.apache.commons.math3.random.RandomGenerator;

/**
 * Copyright Tao, All Rights Reserved.
 * Confidential, do not distribute.
 *
 * Any source code displaying this header must
 * be considered closed source and confidential
 * until the project is released under an open
 * source license.
 */

public abstract class StochasticSimulation extends Simulation {
    protected final RandomGenerator randomGenerator;

    public StochasticSimulation(RandomGenerator randomGenerator) throws NoSuchMethodException {
        super();
        this.randomGenerator = randomGenerator;
    }
}
