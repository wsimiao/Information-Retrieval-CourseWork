package Classes;

public class Document {
	
	protected int docid;
	protected String docno;
	protected double score;
	
	public Document( int docid, String docno, double score ) {
		this.docid = docid;
		this.docno = docno;
		this.score = score;
	}
	
	public int docid() {
		return docid;
	}
	
	public String docno() {
		return docno;
	}
	
	public double score() {
		return score;
	}
	
	public void setDocid( int docid ) {
		this.docid = docid;
	}
	
	public void setDocno( String docno ) {
		this.docno = docno;
	}
	
	public void setScore( double score ) {
		this.score = score;
	}
	
}
