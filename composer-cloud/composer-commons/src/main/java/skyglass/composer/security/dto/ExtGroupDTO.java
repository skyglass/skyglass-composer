package skyglass.composer.security.dto;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 *
 * @author skyglass
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExtGroupDTO implements Serializable {

	private static final long serialVersionUID = 1408555105529359539L;

	private String id;

	private String displayName;

	private ExtGroupNameDTO name;

	List<ExtGroupMemberDTO> members;

	public ExtGroupDTO() {

	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public ExtGroupNameDTO getName() {
		return name;
	}

	public void setName(ExtGroupNameDTO name) {
		this.name = name;
	}

	public List<ExtGroupMemberDTO> getMembers() {
		return members;
	}

	public void setMembers(List<ExtGroupMemberDTO> members) {
		this.members = members;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

}
