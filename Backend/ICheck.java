interface ICheck{
	//data for the check goes here

	//this method updates the checks data
	public void update(Stock stock);

	/*this method performs the actual check passing an Anomaly object back if
	an anomaly has occured, and null if not*/
	public Anomaly check(Stock stock, Client client);

}