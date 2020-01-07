import java.sql.*;
import java.util.Scanner;

public class JDBChw1 {
	public static void main(String[] args) throws Exception {
		Scanner sc = new Scanner(System.in);

		// removal of Timezone and Datetimecode legcay mode parameters from connection
		// URL might be
		// required, depends on MySQL version
		String db = "jdbc:mysql://localhost/sakila?useLegacyDatetimeCode=false&serverTimezone=UTC";
		Class.forName("com.mysql.cj.jdbc.Driver");
		System.out.println("Username:");
		String user = sc.next();
		System.out.println("Password:");
		String pw = sc.next();
		Connection con = DriverManager.getConnection(db, user, pw);
		Statement stmt = con.createStatement();

		char choice = '0';
		while (choice != 'd') {
			System.out.println("Please select from the menu:");
			System.out.println("a.Add actor");
			System.out.println("b.Execute query");
			System.out.println("c.Execute parameter query");
			System.out.println("d.Exit");

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
				System.out.println("Exiting");
				stmt.close();
				con.close();
				break;

			default:
				System.out.println("Not a menu option, please select again\n");
			}
			System.out.println();
		}

	}

	public static void print(PreparedStatement ps, ResultSet rs) throws SQLException {
		ResultSetMetaData metaData = rs.getMetaData();
		int columnCount = metaData.getColumnCount();

		int i = 1;
		int j = 0;
		while (rs.next()) {
			i = 1;
			while (i <= columnCount) {
				System.out.print(rs.getString(i) + " ");
				i++;
			}
			System.out.println("\n");
			j++;
		}
		System.out.println(j + " enteries returned");
		rs.close();
	}

	public static void addActor(Statement stmt) throws SQLException {
		String fname, lname;
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter first name:");
		fname = sc.nextLine();
		System.out.println("Enter last name:");
		lname = sc.nextLine();
		stmt.executeUpdate("INSERT INTO ACTOR(FIRST_NAME, LAST_NAME) VALUES(" + "\"" + fname + "\"" + "," + "\"" + lname
				+ "\"" + ");");
	}

	public static void quary(Statement stmt) throws SQLException {
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter query:");
		String query = sc.nextLine();
		ResultSet rs = null;
		try {
		rs = stmt.executeQuery(query);
		}catch(SQLException e) {
			System.out.println("Illegal query\nReturning to menu");
			return;
		}
		ResultSetMetaData metaData = rs.getMetaData();
		int columnCount = metaData.getColumnCount();
		int i = 1, j = 0; 

		while (rs.next()) {
			i = 1;
			while (i <= columnCount) {
				System.out.print(rs.getString(i) + " ");
				i++;
			}
			System.out.println();
			j++;
		}
		System.out.println("Query returned " + j + "entries");
		rs.close();

	}

	public static void pQuary(Connection con) throws SQLException {
		Scanner sc = new Scanner(System.in);
		Scanner sc2 = new Scanner(System.in);
		char choice;
		ResultSet rs;
		String temp;
		System.out.println("Please select from the menu:");
		System.out.println("a.Find movie by keyword in name");
		System.out.println("b.Find movie by actor name");
		System.out.println("c.Find movie by year");
		System.out.println("d.Find movie by category");
		System.out.println("e.Find movie by acting cast size");
		System.out.println("enter anything else to return");

		choice = sc.next().charAt(0);
		PreparedStatement ps;
		switch (choice) {

		case 'a':
			System.out.println("Enter keyword");
			String kw = sc2.nextLine();
			ps = con.prepareStatement("SELECT TITLE FROM FILM WHERE FILM.TITLE LIKE '%" + kw + "%'");
			rs = ps.executeQuery();
			print(ps, rs); // function to print the result
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
			print(ps, rs);
			break;

		case 'c':
			System.out.println("Enter release year:");
			temp = sc.next();
			ps = con.prepareStatement(
					"SELECT TITLE FROM FILM WHERE RELEASE_YEAR=?");
			ps.setObject(1, temp);
			rs = ps.executeQuery();
			print(ps, rs);
			break;

		case 'd':
			System.out.println("Enter category: ");
			temp = sc.nextLine();
			ps = con.prepareStatement(
					"SELECT TITLE FROM FILM , CATEGORY , FILM_CATEGORY WHERE CATEGORY.NAME=? AND CATEGORY.CATEGORY_ID=FILM_CATEGORY.CATEGORY_ID AND FILM_CATEGORY.FILM_ID=FILM.FILM_ID");
			ps.setObject(1, temp);
			rs = ps.executeQuery();
			print(ps, rs);
			break;

		case 'e':
			System.out.println("Enter number of actors");
			int count = sc.nextInt();
			ps = con.prepareStatement(
					"SELECT TITLE FROM FILM WHERE FILM_ID IN (SELECT ACTOR_ID FROM FILM_ACTOR GROUP BY FILM_ID HAVING COUNT(ACTOR_ID)=?) ");
			ps.setObject(1, count);
			rs = ps.executeQuery();
			print(ps, rs);
			break;

		default:
			System.out.println("Cancelling parameter query");
			return;
		}

	}
}
