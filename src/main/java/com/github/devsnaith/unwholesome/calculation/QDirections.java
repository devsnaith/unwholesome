package com.github.devsnaith.unwholesome.calculation;

import java.util.concurrent.ThreadLocalRandom;

public class QDirections {
	Directions isNORTH;
	Directions isSOUTH;
	Directions isWEST;
	Directions isEAST;

	public QDirections() {
		this.isNORTH = null;
		this.isSOUTH = null;
		this.isWEST = null;
		this.isEAST = null;
	}

	public void setDirection(Directions... arrayOfDirections) {

		byte b;
		int i;
		Directions[] arrayOfDirections1;

		for (i = (arrayOfDirections1 = arrayOfDirections).length, b = 0; b < i;) {
			Directions Direction = arrayOfDirections1[b];
			this.isNORTH = (Direction == Directions.NORTH) ? Directions.NORTH : this.isNORTH;
			this.isSOUTH = (Direction == Directions.SOUTH) ? Directions.SOUTH : this.isSOUTH;
			this.isWEST = (Direction == Directions.WEST) ? Directions.WEST : this.isWEST;
			this.isEAST = (Direction == Directions.EAST) ? Directions.EAST : this.isEAST;
			b++;
		}
	}

	public enum Directions {
		NORTH, SOUTH, WEST, EAST;
	}

	public void removeDirection(Directions Direction) {
		this.isNORTH = (Direction == Directions.NORTH) ? null : this.isNORTH;
		this.isSOUTH = (Direction == Directions.SOUTH) ? null : this.isSOUTH;
		this.isWEST = (Direction == Directions.WEST) ? null : this.isWEST;
		this.isEAST = (Direction == Directions.EAST) ? null : this.isEAST;
	}

	public boolean isDirection(Directions Direction) {
		Directions SingleDirection = (Direction == Directions.NORTH) ? this.isNORTH
				: ((Direction == Directions.SOUTH) ? this.isSOUTH
						: ((Direction == Directions.WEST) ? this.isWEST
								: ((Direction == Directions.EAST) ? this.isEAST : null)));
		if (SingleDirection != null) {
			return true;
		}
		return false;
	}

	public void copy(QDirections DirectionsObject) {
		byte b;
		int i;
		Directions[] arrayOfDirections;
		for (i = (arrayOfDirections = Directions.values()).length, b = 0; b < i;) {
			Directions Direction = arrayOfDirections[b];
			if (DirectionsObject.isDirection(Direction)) {
				setDirection(new Directions[] { Direction });
			} else {
				removeDirection(Direction);
			}
			b++;
		}
	}

	public static Directions getRandomDirection() {
		return Directions.values()[ThreadLocalRandom.current().nextInt((Directions.values()).length)];
	}
}