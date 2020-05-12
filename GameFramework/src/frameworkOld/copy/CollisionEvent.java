package frameworkOld.copy;

public class CollisionEvent extends GameEvent {

	private Model m1, m2;
	
	private CollisionEvent(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}
	
	public CollisionEvent(Model m1, Model m2) {
		super("Collision Event: " + m1 + "<->" + m2);
		this.m1 = m1;
		this.m2 = m2;
		
	}

	public Model getM1() {
		return m1;
	}

	public Model getM2() {
		return m2;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			 return false;
		if (!(obj instanceof CollisionEvent))
			return false;
		CollisionEvent ce = (CollisionEvent) obj;
		
		return m1.equals(ce.m1) && m2.equals(ce.m2) ||
						(m1.equals(ce.m2) && m2.equals(ce.m1));
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return m1 + "<->" + m2;
	}
	
}
