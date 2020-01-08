import java.sql.*;
import java.util.Scanner;

public class JDBChw1 {
	static Scanner sc = new Scanner(System.in);
	static Scanner sc2 = new Scanner(System.in);

	public static void main(String[] args) throws Exception {

		char choice = '0';
		// removal of Timezone and Datetimecode legcay mode parameters from connection
		// URL might be
		// required, depends on MySQL version
		String db = "jdbc:mysql://localhost/sakila?useLegacyDatetimeCode=false&serverTimezone=UTC";
		Class.forName("com.mysql.cj.jdbc.Driver");
		System.out.println("Username:");
		String user = sc.next();
		System.out.println("Password:");
		String pw = sc.next();
		Connection con = null;
		Statement stmt = null;
		try {
			con = DriverManager.getConnection(db, user, pw);
			stmt = con.createStatement();
		} catch (SQLException e) {
			System.out.println("Connection error \nterminating");
			choice = 'd';
		}

		while (choice != 'd') {
			System.out.println("Please select from the menu:");
			System.out.println("a. Add actor");
			System.out.println("b. Execute query");
			System.out.println("c. Execute parameter query");
			System.out.println("d. Exit");

			choice = sc.next().charAt(0);

			switch (choice) {
			case 'a':
				addActor(stmt);
				break;

			case 'b':
				quary(stmt);
				break;

			case 'c':
				pQuary(con);
				break;

			case 'd':
				System.out.println("Shutting down");
				stmt.close();
				con.close();
				sc.close();
				sc2.close();
				break;

			default:
				System.out.println("Not a menu option, please select again\n");
			}
			System.out.println();
		}
	}

	public static void print(ResultSet rs) throws SQLException {
		ResultSetMetaData metaData = rs.getMetaData();
		int columnCount = metaData.getColumnCount();

		int i = 1, j=0;
		
		while (rs.next()) {
			i = 1;
			while (i <= columnCount) {
				System.out.print(rs.getString(i) + " | ");
				i++;
			}
			System.out.println("\n");
			j++;
		}
		System.out.println(j + " enterie(s) returned");
		rs.close();
	}

	public static void addActor(Statement stmt) throws SQLException {
		String fname, lname;
		
		do {
			System.out.println("Enter first name:");
			fname = sc2.nextLine();
			if(fname.length() <= 0) {
				System.out.println("This field cannot be empty\n");
			}
		} while (fname.length() <= 0);
		
		do {
			System.out.println("Enter last name:");
			lname = sc2.nextLine();
			if(lname.length() <= 0) {
				System.out.println("This field cannot be empty\n");
			}
		} while (lname.length() <= 0);
		
		try {
			stmt.executeUpdate("INSERT INTO ACTOR(FIRST_NAME, LAST_NAME) VALUES(" + "\"" + fname + "\"" + "," + "\""
					+ lname + "\"" + ");");
			System.out.println("Entry successfuly added");
		} catch (SQLException e) {
			System.out.println("Error\nentry not added");
			return;
		}
	}

	public static void quary(Statement stmt) throws SQLException {
		System.out.println("Enter query:");
		String query = sc2.nextLine();
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(query);
			print(rs);
		} catch (SQLException e) {
			System.out.println("Illegal query\nReturning to menu");
			return;
		}
	}

	public static void pQuary(Connection con) throws SQLException {
		char choice;
		ResultSet rs;
		String temp;
		System.out.println("Please select from the menu:");
		System.out.println("a. Find movie by keyword in name");
		System.out.println("b. Find movie by actor name");
		System.out.println("c. Find movie by year");
		System.out.println("d. Find movie by category");
		System.out.println("e. Find movie by acting cast size");
		System.out.println("Enter anything else to return");

		choice = sc.next().charAt(0);
		PreparedStatement ps;
		switch (choice) {

		case 'a':
			System.out.println("Enter keyword");
			String kw = sc2.nextLine();
			ps = con.prepareStatement("SELECT TITLE FROM FILM WHERE FILM.TITLE LIKE '%" + kw + "%'");
			rs = ps.executeQuery();
			break;

		case 'b':
			String fname, lname;
			System.out.println("Enter first name:");
			fname = sc2.nextLine();
			System.out.println("Enter last name:");
			lname = sc2.nextLine();
			ps = con.prepareStatement(
					"SELECT TITLE FROM FILM,ACTOR,FILM_ACTOR WHERE ACTOR.ACTOR_ID=FILM_ACTOR.ACTOR_ID AND FILM_ACTOR.FILM_ID=FILM.FILM_ID AND 						ACTOR.FIRST_NAME=? AND ACTOR.LAST_NAME=?");
			ps.setObject(1, fname);
			ps.setObject(2, lname);
			rs = ps.executeQuery();
			break;

		case 'c':
			System.out.println("Enter release year:");
			temp = sc2.next();
			ps = con.prepareStatement("SELECT TITLE FROM FILM WHERE RELEASE_YEAR=?");
			ps.setObject(1, temp);
			rs = ps.executeQuery();
			break;

		case 'd':
			System.out.println("Enter category: ");
			temp = sc2.nextLine();
			ps = con.prepareStatement(
					"SELECT TITLE FROM FILM , CATEGORY , FILM_CATEGORY WHERE CATEGORY.NAME=? AND CATEGORY.CATEGORY_ID=FILM_CATEGORY.CATEGORY_ID AND FILM_CATEGORY.FILM_ID=FILM.FILM_ID");
			ps.setObject(1, temp);
			rs = ps.executeQuery();
			break;

		case 'e':
			System.out.println("Enter number of actors");
			int count = sc2.nextInt();
			ps = con.prepareStatement(
					"SELECT TITLE FROM FILM WHERE FILM_ID IN (SELECT FILM_ID FROM FILM_ACTOR GROUP BY FILM_ID HAVING COUNT(ACTOR_ID)=?)");
			ps.setObject(1, count);
			rs = ps.executeQuery();
			break;

		default:
			System.out.println("Cancelling parameter query");
			return;
		}
		print(rs);
	}
}
