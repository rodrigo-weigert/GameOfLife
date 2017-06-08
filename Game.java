import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.io.*;

public class Game implements Runnable
{
	private final int WORLD_WIDTH, WORLD_HEIGHT, CELL_SIZE, WINDOW_WIDTH, WINDOW_HEIGHT;		//world em células, cell size e window em pixels
	private final GraphicsContext context;
	private final String FILENAME;
	private boolean[][] state, statemem;									//falso para morto, verdadeiro para vivo
	private boolean stop;
	private boolean hasStopped;
	
	private int ticksPerSecond;
	
	private final int[][] dir;
	
	public Game(int w, int h, int cell_size, GraphicsContext context)
	{
		dir = new int[][]{{-1, 0}, {-1, 1}, {0, 1}, {1, 1}, {1, 0}, {1, -1}, {0, -1}, {-1, -1}};
		
		WORLD_WIDTH = w;
		WORLD_HEIGHT = h;
		CELL_SIZE = cell_size;
		this.context = context;
		WINDOW_WIDTH = WORLD_WIDTH*CELL_SIZE;
		WINDOW_HEIGHT = WORLD_HEIGHT*CELL_SIZE;
		state = new boolean[WORLD_HEIGHT][WORLD_WIDTH];
		statemem = new boolean[WORLD_HEIGHT][WORLD_WIDTH];
		stop = true;
		FILENAME = "save.dat";
		ticksPerSecond = 4;
		
		context.setFill(Color.WHITE);
		context.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
	}
	
	private int getCoord(int icell)		//icell é o índice da linha ou coluna da célula, começando de zero
	{
		return icell*CELL_SIZE;
	}
	
	private int getIndex(int coord)		//coord é a cordenada do pixel clicado na tela
	{
		return coord/CELL_SIZE;
	}
	
	private boolean validatePosition(int i, int j)
	{
		return (i >= 0 && j >= 0 && i < WORLD_HEIGHT && j < WORLD_WIDTH);
	}
	
	private void fromMemory()
	{
		for (int i = 0; i < WORLD_HEIGHT; i++)
		{
			for (int j = 0; j < WORLD_WIDTH; j++)
			{
				if (statemem[i][j] != state[i][j])
					setCell(i, j, statemem[i][j]);
			}
		}
		
	}
	
	private void toMemory()
	{
		for (int i = 0; i < WORLD_HEIGHT; i++)
		{
			for (int j = 0; j < WORLD_WIDTH; j++)
			{
					statemem[i][j] = state[i][j];
			}
		}
	}
	
	public synchronized void toFile()
	{
		try 
		{
			DataOutputStream output = new DataOutputStream(new FileOutputStream(FILENAME));
			output.writeInt(WORLD_WIDTH);
			output.writeInt(WORLD_HEIGHT);
			for (int i = 0; i < WORLD_HEIGHT; i++)
			{
				for (int j = 0; j < WORLD_WIDTH; j++)
				{
					output.writeBoolean(state[i][j]);
				}
			}
			output.flush();
			output.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public void fromFile()
	{
		stop();
		try 
		{
			DataInputStream input = new DataInputStream(new FileInputStream(FILENAME));
			int ww = input.readInt();
			int wh = input.readInt();
			
			if (ww > WORLD_WIDTH || wh > WORLD_HEIGHT)
				throw new Exception("Current world size is smaller than the required by the save file.");
			
			for (int i = 0; i < wh; i++)
			{
				for (int j = 0; j < ww; j++)
				{
					boolean cur = input.readBoolean();
					if (cur != state[i][j])
						setCell(i, j, cur);
				}
			}
			input.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public boolean isAlive(int i, int j)
	{	
		return state[i][j];
	}
	
	public int adjCount(int i, int j)
	{
		int ans = 0;
		for (int d = 0; d < 8; d++)
		{
			int ii = i + dir[d][0];
			int jj = j + dir[d][1];
			
			if (validatePosition(ii, jj))
			{
				if (isAlive(ii, jj))
					ans++;
			}
		}
		return ans;
	}
	
	public void setCell(int i, int j, boolean alive)
	{
		if (!validatePosition(i, j))
			return;
		
		state[i][j] = alive;
		int x = getCoord(j);
		int y = getCoord(i);
		
		if (alive)
			context.setFill(Color.BLACK);
		else
			context.setFill(Color.WHITE);
			
		context.fillRect(x, y, CELL_SIZE, CELL_SIZE);
	}
	
	public void setCellByClick(int x, int y, boolean alive)
	{
		int i = getIndex(y);
		int j = getIndex(x);
		
		setCell(i, j, alive);
	
	}
	
	public synchronized void tick()
	{
		class Task
		{
			int i, j;
			boolean state;
			
			Task(int i, int j, boolean state)
			{
				this.i = i;
				this.j = j;
				this.state = state;
			}
			
			void execute()
			{
				setCell(i, j, state);
			}
		}
		
		ArrayList<Task> todo = new ArrayList<Task>();
		
		for (int i = 0; i < WORLD_HEIGHT; i++)
		{
			for (int j = 0; j < WORLD_WIDTH; j++)
			{
				int adj = adjCount(i, j);
				if (isAlive(i, j))
				{
					if (adj < 2 || adj > 3)
						todo.add(new Task(i, j, false));
				}
				else
				{
					if (adj == 3)
						todo.add(new Task(i, j, true));
				}
			}
		}
		
		for (Task t : todo)
		{
			t.execute();
		}
	}
	
	public synchronized void setTicksPerSecond(int value)
	{
		ticksPerSecond = value;
	}
	
	public void clear()
	{
		stop();
		for (int i = 0; i < WORLD_HEIGHT; i++)
		{
			for (int j = 0; j < WORLD_WIDTH; j++)
			{
				if (isAlive(i, j))
					setCell(i, j, false);
			}
		}
	}
	
	public void reset()
	{
		stop();
		fromMemory();
	}
	
	public void stop()
	{
		if (stop)
			return;
		
		stop = true;
		try 
		{
			Thread.sleep(500);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void run()
	{
		stop = false;
		toMemory();
		while(!stop)
		{
			try
			{
				Thread.sleep(1000/ticksPerSecond);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			tick();
		}
	}
	
}
