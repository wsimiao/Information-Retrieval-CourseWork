package Classes;
import java.util.List;
public class Query {
	//you can modify this class

	private List<String> queryContent;	
	private String topicId;	
	
	
	
	public List<String> GetQueryContent() {
		return queryContent;
	}
	public String GetTopicId() {
		return topicId;
	}
	public void SetQueryContent(List<String> content){
		queryContent=content;
	}	
	public void SetTopicId(String id){
		topicId=id;
	}
}
