DROP TABLE stocks;

CREATE TABLE stocks (
	Time		varchar(26)		NOT NULL,
	Buyer		varchar(50)		NOT NULL,
	Seller		varchar(50)		NOT NULL,
	Price		float(10, 2)	NOT NULL,
	Size		int UNSIGNED	NOT NULL,
	Currency	varchar(4)		NOT NULL,
	Symbol		varchar(40)		NOT NULL,
	Sector		varchar(40)		NOT NULL,
	Bid			float(10, 2)	NOT NULL,
	Ask			float(10, 2)	NOT NULL
	-- PRIMARY KEY (Time)		--TODO decide on primary key as time can be duplicated
);