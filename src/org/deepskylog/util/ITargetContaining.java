/* ====================================================================
 * /TargetContaining.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package org.deepskylog.util;

import java.util.List;

/**
 * Basically a marker interface that indicates that this ITarget
 * implementation depends on other ITargets.<br>
 * Example: MultipleStarSystems
 * A MultipleStarSystem should implement this interface as it
 * refers to other ITargets (TargetStar) elements.
 * 
 * @author D036774
 * @since 2.0_p1
 *
 */
public interface ITargetContaining {

	@SuppressWarnings("rawtypes")
	public List getComponentTargets(ITarget[] targets);
	
}
