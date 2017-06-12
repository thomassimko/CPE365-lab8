-- General Q1

SELECT Start, End, PriceInc, PriceDec
FROM

	(SELECT COUNT(DISTINCT Ticker) as Start
	FROM Prices p
	WHERE p.Day <= ALL (SELECT Day from Prices where YEAR(Day) = 2016) and
      YEAR(Day) = 2016) q1,

	(SELECT COUNT(DISTINCT Ticker) as End
	FROM Prices p
	WHERE p.Day >= ALL (SELECT Day from Prices where YEAR(Day) = 2016) and
      YEAR(Day) = 2016) q2,

	(SELECT COUNT(DISTINCT p1.Ticker) as PriceInc
	FROM Prices p1, Prices p2
	WHERE p1.ticker = p2.ticker and p1.Day >= ALL (SELECT Day from Prices where YEAR(Day) = 2015) and YEAR(p1.Day) = 2015 and p2.Day >= ALL (SELECT Day from Prices where YEAR(Day) = 2016) and YEAR(p2.Day) = 2016 and p1.Close < p2.Close) q3,

	(SELECT COUNT(DISTINCT p1.Ticker) as PriceDec
	FROM Prices p1, Prices p2
	WHERE p1.ticker = p2.ticker and p1.Day >= ALL (SELECT Day from Prices where YEAR(Day) = 2015) and YEAR(p1.Day) = 2015 and p2.Day >= ALL (SELECT Day from Prices where YEAR(Day) = 2016) and YEAR(p2.Day) = 2016 and p1.Close > p2.Close) q4

;

-- General Q2

SELECT Ticker
FROM Prices p
WHERE YEAR(p.day) = 2016
GROUP BY Ticker
ORDER BY SUM(p.volume) DESC
LIMIT 10;


-- General Q3

SELECT AbsoluteInc, RelativeInc
FROM
   (SELECT AbsoluteInc, @r1 := @r1 + 1 as num
   FROM
      (SELECT ticker as AbsoluteInc
      FROM
         (SELECT p.ticker, p.open
         FROM Prices p,
            (SELECT p.ticker, min(Day) as Day
            FROM Prices p
            GROUP BY p.ticker) l
         WHERE p.ticker = l.ticker and l.Day = p.Day) t1

         NATURAL JOIN

         (SELECT p.ticker, p.close
         FROM Prices p,
            (SELECT p.ticker, max(Day) as Day
            FROM Prices p
            GROUP BY p.ticker) l
         WHERE p.ticker = l.ticker and l.Day = p.Day) t2
      ORDER BY (close - open) DESC
      LIMIT 5) a,

      (SELECT @r1 := 0) var
   ) t1,

   (SELECT RelativeInc, @r2 := @r2 + 1 as num
   FROM
      (SELECT ticker as RelativeInc
      FROM
         (SELECT p.ticker, p.open
         FROM Prices p,
            (SELECT p.ticker, min(Day) as Day
            FROM Prices p
            GROUP BY p.ticker) l
         WHERE p.ticker = l.ticker and l.Day = p.Day) t1

         NATURAL JOIN

         (SELECT p.ticker, p.close
         FROM Prices p,
            (SELECT p.ticker, max(Day) as Day
            FROM Prices p
            GROUP BY p.ticker) l
         WHERE p.ticker = l.ticker and l.Day = p.Day) t2

      ORDER BY (close / open) DESC
      LIMIT 5) r,

      (SELECT @r2 := 0) var
   ) t2
WHERE t1.num = t2.num;




