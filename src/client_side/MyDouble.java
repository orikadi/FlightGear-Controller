package client_side;

//mutable double for hash map
public class MyDouble {
	private Double value; //Double so null is possible
	private String path;
	
	 public MyDouble() {
		 value = null;
		 path = null;
	 }
	 public MyDouble(double value) {
	     this.value = value;
	     path = null;
	       //linked = false;
	 }
	 public MyDouble(double value, String path) {
		 this.value = value;
		 this.path = path;
		// linked = true;
	 }
	 public void setValue(double value) {
			 this.value = value;
	 }
	 public Double getValue() {
	     return value;
	 }

	  public String getPath() {
	  	 return path;
	  }
	    
}
