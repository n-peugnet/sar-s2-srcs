package srcs.service.calculatrice;

import java.io.Serializable;

public interface Calculatrice {
	public int add(int a, int b);
	public int sous(int a, int b);
	public int mult(int a, int b);
	public ResDiv div(int a, int b);
	
	public static class ResDiv implements Serializable {
		/**
		 * Version
		 */
		private static final long serialVersionUID = 1L;
		protected int quotient;
		protected int reste;
		
		public ResDiv(int quotient, int reste) {
			this.quotient = quotient;
			this.reste = reste;
		}

		public int getQuotient() {
			return quotient;
		}
		
		public int getReste() {
			return reste;
		}
	}
}
