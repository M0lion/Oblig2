import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.Stack;
import java.io.PrintStream;


public class Graph 
{
	private Task []Tasks;
	private Edge [][]Edges;
	
	int countTasks;
	
	static int finished = 0;
	
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
		
		if(!isGraphCyclic())
		{
			for(Task task : Tasks)
				if(task.earliestStart == -1)
					task.calcEarliestStartTime();
			
			for(Task task : Tasks)
				task.calcLatestStartTime();
		}
	}

	//simulates doing the tasks and prints result
	public void start(PrintStream out)
	{
		int time = 0;
		int nextUpdate = 0;
		int tasksToComplete = countTasks;
		int manpower = 0;
		int[] runningtasks = new int[Tasks.length]; //0: idle, 1: running, 2: finished

		Stack<Task> started = new Stack<Task>();
		Stack<Task> finished = new Stack<Task>();
		
		while(tasksToComplete > 0)
		{
			nextUpdate = Integer.MAX_VALUE;
			
			for(int i = 0; i < countTasks; i++)
			{
				if(runningtasks[i] == 0 && Tasks[i].earliestStart <= time)
				{
					runningtasks[i] = 1;
					started.add(Tasks[i]);
					manpower += Tasks[i].staff;
				}
				if(runningtasks[i] == 1 && Tasks[i].earliestStart + Tasks[i].time <= time)
				{
					runningtasks[i] = 2;
					finished.add(Tasks[i]);
					tasksToComplete--;
					manpower -= Tasks[i].staff;
				}
				
			}
			
			if(!started.empty() || !finished.empty())
			{
				out.println("Time: " + time + "\tCurrent staff: " + manpower);

				while(!finished.empty())
					out.println("\tFinished: " + finished.pop().id);
				while(!started.empty())
					out.println("\tStarted: " + started.pop().id);
			}
			
			time++;
		}
		
		out.println();
	}
	
	public void printCritcals(PrintStream out)
	{
		for(int i = 0; i < countTasks; i++)
		{
			if(Tasks[i].earliestStart == Tasks[i].latestStart)
				Tasks[i].print(out);
		}
	}
	
	//checks if graph is cyclic, if it is prints out the nodes in the found cycle
	private boolean isGraphCyclic()
	{
		if(Tasks[0].checkForLoop(true) == 0)
			if(Tasks[0].checkForLoop(true) == 0)
				return false;
		return true;
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
		int earliestStart = -1;
		int latestStart = -1;
		int slack = -1;
		int cntOutEdges = 0;
		Edge []outEdges;
		Edge []inEdges;
		int cntPredecessors ;
		
		//status for finding loops 0:not visited, 1: in progress, 2: found loop
		int status = 0;
		
		public Task(){};
		
		public Task(String input, Graph graph)
		{
			Initialize(input, graph);
		}
		
		private void print(PrintStream out)
		{
			out.println("ID: " + id + "\nName: " + name + "\nTime: " +  time + "\nStaff: " + "\nSlack: 0\nLatest start: " + latestStart);
			out.print("Tasks depending on this:");
			
			for(int i = 0; i < outEdges.length; i++)
			{
				out.print(" " + outEdges[i].to);
			}
			out.println("\n");
		}
		
		private void calcEarliestStartTime()
		{
			earliestStart = 0;
			
			for(Edge edge : inEdges)
			{
				if(edge != null)
				{
					if(edge.fromTask.earliestStart == -1)
						edge.fromTask.calcEarliestStartTime();
					
					int predecessorFinished = edge.fromTask.earliestStart + edge.fromTask.time;
					
					if(predecessorFinished > earliestStart)
						earliestStart = predecessorFinished;
				}
			}
			
			if(finished < earliestStart + time)
				finished = earliestStart + time;
		}

		private void calcLatestStartTime()
		{
			latestStart = earliestStart;
			
			for(Edge edge : outEdges)
			{				
				int nextStart = edge.toTask.earliestStart;
				
				if(nextStart > latestStart + time)
					latestStart = nextStart - time;
			}
			
			if(outEdges.length == 0)
			{
				latestStart = finished - time;
			}
			
			slack = latestStart - earliestStart;
		}
		
		//recursively checks for loops in given direction(true with edges, false against edges), returns 0 for no loop, 1 for loop
		private int checkForLoop(boolean direction)
		{
			switch(status)
			{
				case 0:
					status = 1;
					
					Task[] tasks;
					
					if(direction)
						tasks = getOutTasks();
					else
						tasks = getInTasks();
					
					for(int i = 0; i < tasks.length; i++)
					{
						int result = tasks[i].checkForLoop(direction);
						
						if(result == 2)
							System.out.println(id);
						
						if(result == 2 || result == 1)
						{						
							if(status == 2)
								result = 1;
							
							status = 0;
							return result;
						}
					}
					
					break;
				case 1:
					status = 2;
					System.out.println("Loop found in theese tasks:");
					return 2;
			}
			
			status = 0;
			return 0;
		}
		
		private Task [] getOutTasks()
		{
			Task[] tasks = new Task[outEdges.length];
			
			for(int i = 0; i < tasks.length; i++)
			{
				tasks[i] = outEdges[i].toTask;
			}
			
			return tasks;
		}
		
		private Task [] getInTasks()
		{
			Task[] tasks = new Task[outEdges.length];
			
			for(int i = 0; i < tasks.length; i++)
			{
				tasks[i] = inEdges[i].fromTask;
			}
			
			return tasks;
		}
		
		public void Initialize(String input, Graph graph)
		{
			String []tokens = input.split("\\s+");
			
			id = Integer.parseInt(tokens[0]); 
			name = tokens[1];
			time = Integer.parseInt(tokens[2]);
			staff = Integer.parseInt(tokens[3]);
			
			inEdges = new Edge[tokens.length - 4];
			int edge = 0;
			for(int i = 4; i < tokens.length; i++)
			{
				if(Integer.parseInt(tokens[i]) != 0)
				{
					addInEdge(Integer.parseInt(tokens[i]), edge, graph);
					edge++;
				}
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
