-- General Q1

SELECT Start, End, PriceInc, PriceDec
FROM

	(SELECT COUNT(DISTINCT Ticker) as Start
	FROM Prices p
	WHERE p.Day <= ALL (SELECT Day from Prices where YEAR(Day) = 2016) and YEAR(Day) = 2016) q1, 

	(SELECT COUNT(DISTINCT Ticker) as End
	FROM Prices p
	WHERE p.Day >= ALL (SELECT Day from Prices where YEAR(Day) = 2016) and YEAR(Day) = 2016) q2, 

	(SELECT COUNT(DISTINCT p1.Ticker) as PriceInc
	FROM Prices p1, Prices p2
	WHERE p1.ticker = p2.ticker and p1.Day >= ALL (SELECT Day from Prices where YEAR(Day) = 2015) and YEAR(p1.Day) = 2015 and p2.Day >= ALL (SELECT Day from Prices where YEAR(Day) = 2016) and YEAR(p2.Day) = 2016 and p1.Close < p2.Close) q3,

	(SELECT COUNT(DISTINCT p1.Ticker) as PriceDec
	FROM Prices p1, Prices p2
	WHERE p1.ticker = p2.ticker and p1.Day >= ALL (SELECT Day from Prices where YEAR(Day) = 2015) and YEAR(p1.Day) = 2015 and p2.Day >= ALL (SELECT Day from Prices where YEAR(Day) = 2016) and YEAR(p2.Day) = 2016 and p1.Close > p2.Close) q4

;
