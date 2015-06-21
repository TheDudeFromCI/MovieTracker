package me.ci;

public enum Rating{
	G("G"),
	PG("PG"),
	PG_13("PG-13"),
	PG_14("PG-14"),
	R("R"),
	NC_17("NC-17"),
	NR("Not Rated");
	private final String title;
	private Rating(String title){
		this.title = title;
	}
	@Override
	public String toString(){
		return title;
	}
}