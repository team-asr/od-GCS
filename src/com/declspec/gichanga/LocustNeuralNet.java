package com.declspec.gichanga;

/**
 * 
 * @author moses gichangA
 * 
 * Neural Network to identify desert locusts. I propose using Markovian processes for prediction modelling,
 * though honestly this may be a bit of overkill
 *
 */
public abstract class LocustNeuralNet {
	/**
     * Creates a duplicate object of the HMM.
     *
     * @return An IHHM that contains the same date as this object.
     * @throws CloneNotSupportedException An exception such that classes lower
     * in the hierarchy can fail to clone.
     */
    public abstract Locust clone() throws CloneNotSupportedException;

    /**
     * Returns the probability associated with the transition going from state
     * <i>i</i> to state <i>j</i> (<i>a<sub>i,j</sub></i>).
     *
     * @param i The first state number such that
     * <code>0 &le; i &lt; nbStates()</code>.
     * @param j The second state number such that
     * <code>0 &le; j &lt; nbStates()</code>.
     * @return The probability associated to the transition going from
     * <code>i</code> to state <code>j</code>.
     */
    public abstract double getAij(int i, int j);


}

interface Locust
{}