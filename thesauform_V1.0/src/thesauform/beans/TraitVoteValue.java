package thesauform.beans;

//TODO add comment to vote, need to modify all the structure
public class TraitVoteValue {
	
	private Integer voteValue;
	private String voteComment;
	private static final String MISSING_VOTE = "Vote is empty";
	private static final String MISSING_COMMENT = "Comment is empty";

	public TraitVoteValue(Integer vote,String comment) throws Exception{
		try {
			if(valideVote(vote)) {
				setVoteValue(vote);
			}
		}
		catch(Exception e) {
			throw new Exception(MISSING_VOTE);
		}
		try {
			if(valideComment(comment)){
				setVoteComment(comment);
			}
		}
		catch(Exception e) {
			this.voteComment = null;
		}
	}
	
	public String toString() {
		return ("(vote: " + getVoteValue() + " comment: " + getComment() + ")");
	}

	public boolean valideVote(Integer vote) throws Exception {
		boolean returnVal = false;
		if (vote != null) {
			returnVal = true;
		} else {
			throw new Exception(MISSING_VOTE);
		}
		return (returnVal);
	}

	public boolean valideComment(String comment) throws Exception {
		boolean returnVal = false;
		if (comment != null && !comment.isEmpty()) {
			returnVal = true;
		} else {
			throw new Exception(MISSING_COMMENT);
		}
		return (returnVal);
	}

	public Integer getVoteValue() {
		return (this.voteValue);
	}

	public String getComment() {
		return (this.voteComment);
	}

	public void setVoteValue(Integer vote) throws Exception {
		try {
			if (valideVote(vote)) {
				this.voteValue = vote;
			}
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}

	public void setVoteComment(String comment) throws Exception {
		try {
			if (valideComment(comment)) {
				this.voteComment = comment;
			}
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}
}
