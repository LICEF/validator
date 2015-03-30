public class ISBN13 {

	public enum ISBNnum {

		SIZE(13);
		private final int value;

		ISBNnum(int number) {
			this.value = number;
		}
	}

	public static int calculateCheckDigit(String isbnNo) {
		int checkDigit = 0;
		int checkSum = 0;
		int digit = 0;  // holds each digit copied from string

		// remove all hyphens
		String isbnNumber = isbnNo.replaceAll("-", "");

		// do we have 12 characters?
		if (isbnNumber.length() != ISBNnum.SIZE.value - 1) {
			return -1;
		}

		//now compute the checkSum
		for (int i = 0; i < isbnNumber.length(); i++) {
			if (Character.isDigit(isbnNumber.charAt(i))) {
				digit = Character.digit(isbnNumber.charAt(i), 10);

				if (i % 2 == 1) {
					digit *= 3;
				}

				checkSum += digit;
			} else {
				return -1;
			}
		}

		// compute the checkDigit
		checkDigit = 10 - checkSum % 10;

		checkSum += checkDigit;

		if (checkSum == 10) {
			checkDigit = 0;
		}

		if (checkSum % 10 == 0) {
			return checkDigit;
		}

		return -1;
	}

	public static boolean is_ISBN13_Valid(String isbnNo) {
		int checkSum = 0;  // initialize
		int digit = 0;  // holds each digit copied from string

		String isbnNumber = isbnNo.replaceAll("-", "");  // remove all hyphens

		// do we have 13 characters?
		if (isbnNumber.length() != ISBNnum.SIZE.value) {
			return false;
		}

		//now compute the checkSum
		for (int i = 0; i < ISBNnum.SIZE.value; i++) {
			if (Character.isDigit(isbnNumber.charAt(i))) {
				digit = Character.digit(isbnNumber.charAt(i), 10);

				if (i % 2 == 1) {
					digit *= 3;
				}

				checkSum += digit;
			} else {
				return false;
			}
		}

		// should be modulo 10
		if (checkSum % 10 == 0) {
			return true;
		}

		return false;
	}


	public static void main(String[] args) {
		int result;

		String testISBN13 = "000000000000";
		String goodISBN13 = "978-0-306-40615-";
		String wholeISBN13 = "978-0-306-40615-7";

		if (ISBN13.is_ISBN13_Valid(wholeISBN13)) {
			System.out.println("Yes");
		} else {
			System.out.println("No");
		}

		result = ISBN13.calculateCheckDigit(goodISBN13);

		if (result >= 0) {
			System.out.println("The check digit for " + goodISBN13 + " is " + result);
		} else {
			System.out.println(goodISBN13 + " is not a valid number");
		}
	}
}
