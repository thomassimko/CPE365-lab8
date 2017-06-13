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


SELECT DATEFORMAT(p.day, '%y' ) as Year, p.ticker, SUM(volume), AVG(close), AVG(volume)
FROM Prices p
WHERE ticker='KLAC'
GROUP BY year, ticker;

-- General Q2

SELECT Ticker
FROM Prices p
WHERE YEAR(p.day) = 2016
GROUP BY Ticker
ORDER BY SUM(p.volume) DESC
LIMIT 10;


-- General Q3

SELECT absYears.Year, absYears.Place, absYears.ticker as Absolute, relYears.ticker as Relative
FROM (SELECT abs1.year, abs1.ticker, abs1.Absolute, count(*) as Place
   FROM (SELECT tDays.year, tDays.ticker, ap2.Close-ap1.Open as Absolute
      FROM (((SELECT YEAR(day) as year, ticker, min(day) as YearOpen, max(day) as YearClose
         FROM AdjustedPrices p
         GROUP BY year, ticker) tDays JOIN AdjustedPrices ap1 
            on (tDays.YearOpen=ap1.day and tDays.ticker=ap1.ticker))
         JOIN AdjustedPrices ap2 ON (tDays.ticker=ap2.ticker and tDays.YearClose=ap2.day))
      GROUP BY year, ticker) abs1,
      (SELECT tDays.year, tDays.ticker, ap2.Close-ap1.Open as Absolute
      FROM (((SELECT YEAR(day) as year, ticker, min(day) as YearOpen, max(day) as YearClose
         FROM AdjustedPrices p
         GROUP BY year, ticker) tDays JOIN AdjustedPrices ap1 
            on (tDays.YearOpen=ap1.day and tDays.ticker=ap1.ticker))
         JOIN AdjustedPrices ap2 ON (tDays.ticker=ap2.ticker and tDays.YearClose=ap2.day))
      GROUP BY year, ticker) abs2
   WHERE abs1.year=abs2.year and abs1.Absolute<=abs2.Absolute
   GROUP BY abs1.year, abs1.ticker
   HAVING Place <= 5
   ORDER BY abs1.year, abs1.Absolute, Place DESC
   ) absYears
JOIN
   (SELECT rel1.year, rel1.ticker, rel1.Relative, count(*) as Place
   FROM (SELECT tDays.year, tDays.ticker, 100*(ap2.Close/ap1.Open) as Relative
      FROM (((SELECT YEAR(day) as year, ticker, min(day) as YearOpen, max(day) as YearClose
         FROM AdjustedPrices p
         GROUP BY year, ticker) tDays JOIN AdjustedPrices ap1 
            on (tDays.YearOpen=ap1.day and tDays.ticker=ap1.ticker))
         JOIN AdjustedPrices ap2 ON (tDays.ticker=ap2.ticker and tDays.YearClose=ap2.day))
      GROUP BY year, ticker) rel1,
      (SELECT tDays.year, tDays.ticker, 100*(ap2.Close/ap1.Open) as Relative
      FROM (((SELECT YEAR(day) as year, ticker, min(day) as YearOpen, max(day) as YearClose
         FROM AdjustedPrices p
         GROUP BY year, ticker) tDays JOIN AdjustedPrices ap1 
            on (tDays.YearOpen=ap1.day and tDays.ticker=ap1.ticker))
         JOIN AdjustedPrices ap2 ON (tDays.ticker=ap2.ticker and tDays.YearClose=ap2.day))
      GROUP BY year, ticker) rel2
   WHERE rel1.year=rel2.year and rel1.Relative<=rel2.Relative
   GROUP BY rel1.year, rel1.ticker
   HAVING Place <= 5
   ORDER BY rel1.year, Place DESC
   )relYears on (absYears.year=relYears.Year and absYears.Place=relYears.Place)
ORDER BY year, Place;








=======
-- Individual
SELECT s.ticker, s.name, MIN(p.DAY), MAX(p.Day)
FROM Securities s join Prices p on s.ticker=p.ticker
WHERE s.ticker='KLAC'
GROUP BY s.ticker;
