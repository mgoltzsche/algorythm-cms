package de.algorythm.cms.common.model.entity.impl;

public abstract class AbstractMergeable {

	protected abstract String getMergeableId();
	
	@Override
	public int hashCode() {
		final String mergeableId = getMergeableId();
		
		return 31 + (mergeableId == null ? 0 : mergeableId.hashCode());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final AbstractMergeable other = (AbstractMergeable) obj;
		final String mergeableId = getMergeableId();
		if (mergeableId == null) {
			if (other.getMergeableId() != null)
				return false;
		} else if (!mergeableId.equals(other.getMergeableId()))
			return false;
		return true;
	}
}
