import java.io.IOException;
import java.io.File;
import java.io.PrintStream;


public class Oblig2 {
	public static void main(String []args) throws java.io.FileNotFoundException
	{
		if(!(new File(args[0]).isFile()))
		{
			System.out.println("Cannot open file '" + args[0] + "'");
			return;
		}

		PrintStream out = new PrintStream("out.txt");

		Graph graph = new Graph(args[0]);
		graph.start(out);
		graph.printCritcals(out);

		out.flush();
	}
}
