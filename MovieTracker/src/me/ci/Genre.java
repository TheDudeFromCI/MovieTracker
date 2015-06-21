package me.ci;

public enum Genre{
	Action,
	Adventure,
	Horror,
	Family,
	Kids,
	Comedy,
	Romance,
	Faith,
	Inspirational,
	Action_Comedy,
	Slapstick_Comedy,
	Action_Adventure,
	Action_Horror,
	Thriller;
	@Override
	public String toString(){
		return name().replace('_', ' ');
	}
}