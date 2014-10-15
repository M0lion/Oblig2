import java.io.File;
import java.io.IOException;
import java.util.Scanner;


public class Graph 
{
	private Task []Tasks;
	private Edge [][]Edges;
	
	int countTasks;
	
	public Graph(String file)
	{	
		Scanner reader = null;
		
		try
		{
			reader = new Scanner(new File(file));
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		
		countTasks = Integer.parseInt(reader.nextLine());
		reader.nextLine();
		
		Tasks = new Task[countTasks];
		Edges = new Edge[countTasks][countTasks];
		
		for(int i = 0; i < countTasks; i++)
		{
			Tasks[i] = new Task();
		}
		
		for(int i = 0; i < countTasks; i++)
		{
			Tasks[i].Initialize(reader.nextLine(), this);
		}
		
		for(int i = 0; i < countTasks; i++)
		{
			Tasks[i].initOutEdges(this);
			for(int j = 0; j < countTasks; j++)
			{
				Edge edge = Edges[i][j];
				if(edge != null)
				{
					Tasks[i].addOutEdge(edge);
				}
			}
		}
	}
	
	public Task getTask(int task) {		
		if(Tasks[task - 1] == null)
			Tasks[task - 1] = new Task();
		
		return Tasks[task - 1];
	}	
	
	public Edge getEdge(int fromEdge, int toEdge)
	{
		fromEdge--;
		toEdge--;
		return Edges[fromEdge][toEdge];
	}
	
	//returns the edge from fromTask to toTask, creates the edge if it does not exist
	public Edge createEdge(int fromTask, int toTask) {
		if(Edges[fromTask - 1][toTask - 1] == null)
		{
			return new Edge(fromTask, toTask, this);
		}
		return Edges[fromTask - 1][toTask - 1];
	}
	
	public class Task
	{
		int id , time , staff ;
		String name ;
		int earliestStart , latestStart;
		int cntOutEdges = 0;
		Edge []outEdges;
		Edge []inEdges;
		int cntPredecessors ;
		
		public Task(){};
		
		public Task(String input, Graph graph)
		{
			Initialize(input, graph);
		}
		
		public void Initialize(String input, Graph graph)
		{
			String []lines = input.split("\\s+");
			
			id = Integer.parseInt(lines[0]); 
			name = lines[1];
			time = Integer.parseInt(lines[2]);
			staff = Integer.parseInt(lines[3]);
			
			inEdges = new Edge[lines.length - 4];
			int edge = 0;
			for(int i = 4; i < lines.length; i++)
			{
				if(Integer.parseInt(lines[i]) != 0)
					addInEdge(Integer.parseInt(lines[i]), edge, graph);
			}	
		}
		
		//add in-edge to this and let other task know
		private void addInEdge(int task, int i, Graph graph)
		{
			inEdges[i] = graph.createEdge(task, id);
			//graph.getTask(task).cntOutEdges++;
		}
		
		private void addOutEdge(Edge edge)
		{
			int i = 0;
			while(outEdges[i] != null)
			{
				i++;
			}
			outEdges[i] = edge;
		}
		
		private void initOutEdges(Graph graph)
		{
			cntOutEdges = 0;
			
			for(int i = 0; i < graph.Edges[id - 1].length; i++)
			{
				if(Edges[id - 1][i] != null)
					cntOutEdges++;
			}
			
			outEdges = new Edge[cntOutEdges];
		}
		
		public void print(int t)
		{
			for(int i = 0; i < t; i++)
				System.out.print("\t");
			
			System.out.println((id) + name);
			int a = Edges.length;
			for(int i = 0; i < outEdges.length; i++)
				outEdges[i].toTask.print(t + 1);
		}
	}
	
	public class Edge
	{
		private int from, to;
		public Task fromTask, toTask;
		
		public Edge(int from, int to, Graph graph)
		{	
			this.from = from;
			this.to = to;
			
			fromTask = graph.getTask(from);
			toTask = graph.getTask(to);
			
			graph.Edges[from - 1][to - 1] = this;
		}
		
		public Task getTo()
		{
			return toTask;
		}
		
		public Task getFrom()
		{
			return fromTask;
		}
	}
}
