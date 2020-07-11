package skyglass.composer.security.domain;

public enum OperationType {

	None(0), Read(1), Write(2), Create(1), Update(1), Delete(1), ChangeOwner(1);

	private int rank;

	private OperationType(int rank) {
		this.rank = rank;
	}

	public int getRank() {
		return rank;
	}
}
