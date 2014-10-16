import java.io.IOException;
import java.io.File;


public class Oblig2 {
	public static void main(String []args)
	{
		if(!(new File(args[0]).isFile()))
		{
			System.out.println("Cannot open file '" + args[0] + "'");
			return;
		}

		Graph graph = new Graph(args[0]);
		graph.start();
		graph.printCritcals();
	}
}
