package skyglass.composer.security.domain;

/**
 * Represents all permission types for operations.
 * 
 * @author skyglass
 *
 */
public enum PermissionType {

	Global(0), Role(1), Local(2);

	private int rank;

	private PermissionType(int rank) {
		this.rank = rank;
	}

	public int getRank() {
		return rank;
	}
}
