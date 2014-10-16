import java.io.IOException;


public class Main {
	public static void main(String []args)
	{
		Graph graph = new Graph("buildhouse1.txt");
		graph.start();
		graph.printCritcals();
	}
}
