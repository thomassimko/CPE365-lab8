# CPE365-lab8

Thomas Simko and Mitchel Davis
tjsimko@calpoly.edu
mdavis60@calpoly.edu

Running the program:

   javac *.java

   java Driver [ticker name]


 GENERAL STOCK ANALYSIS METRICS

   For all analysis problems the Relative Growth 
      (Closing Price of Period / Opening Price of Period)
   was used.


   For generating the report based on Sector, the Sector's relative growth is
   compared to the entire stock excahnge growth.  The calculation is the 
   (sector's relative growth - overall realtive growth).  The resulting value
   is mapped to a report based on the table below:

   less than -.1 : "Tanking it."
   -.1 to -.05 : "Diminishing returns."
   -.05 to 0 : "Trying to hold on."
   0 to .05 : "Showing resilience."
   .05 to .08 : "Slowly growing."
   .08 to .12 : "Doing quite well.";
   .12 to .15 : "Rapid growth."
   .15 and up : "Significantly Outperforming."

   These values multipled by 100 show the percentage of the sector's growth in
   comparison to the market as a whole.  We determined that falling behind the 
   market by 10% is a major decrease and exceeding the market by 15% shows that 
   the sector is performing very well.


 INDIVIDUAL STOCK ANALYSIS METRICS

For all analysis problems the Relative Growth 
   100 * (1-(Closing Price of Period/ Opening Price of Period))
was used.

For analysis of individual stocks, the relative growth was anyalyzed on a 
monthly basis against the stocks corresponding Sector and Industry group.

For Predictions the the following information was used.

The individual stock's relative growth (s), the industry's relative growth (i),
and growth of the stock compared to the industry (s-i).  Growth for the 
industry was averaged by month to match the stocks time frame.

In order to generate a buy, sell, or hold signal at a specific date, the 
stock's and industry's performance over the last six months was considered.

For each month the following calculations were made.

if( abs(statistic) < 0.75 ) -> 0
else if (s > 0) -> 1
else -> -1

0.75 is an arbitrary number used to signify normal slight flucuations in
stock price.

Thus for each month two numbers were produced that were either -1,0, or 1
These numbers were then summed to produce a monthly aggregate number in the
range [-2, 2] that were then reduced to the range [-1,1] in the following way:

[2,1]    ->  1
[0]      ->  0
[-1, -2] -> -1

When each of the six months performance were calculated the adjusted aggBHS
signals were summed.  This produced an integer in the range [-6, 6]. From 
here a BUY/HOLD/SELL signal was produced.  The mapping for these ranges and 
results are as follows.

[ -6 , -4 ]  --> SELL
[ -3 , 3  ]  --> HOLD
[ 4  , 6  ]  --> BUY

To analyze the stocks actual performance after the specified date, a similar
procedure was used.  Instead of considering 6 months, only the 3 months 
following the prediction were considered.  The same metrics were used.  The
range of numbers produced from the sum of the aggBHS signals was [ -3, 3].
The actual BUY/HOLD/SELL signal was produced using the following mapping:

[ -3 , -2 ]  --> SELL
[ -1 ,  1 ]  --> HOLD
[  2 ,  3 ]  --> BUY

When comparing the actual to predicted performances, the messages produced on
the HTML page are self explanitory and can be seen in Analyzer.java 
performanceAnalysis function.

Here is an example of how this process works up to summing adjusted aggregate
BHS numbers:

EX
+------+-------+--------------------+---------------------+-------------------+
| year | month | StockChange  (s)   | SectorChange  (i)   | Difference (s-i)  |
+------+-------+--------------------+---------------------+-------------------+
| 2010 |     1 |   22.9508143753641 | -0.8588145839480319 | 23.80962895931216 |
| 2010 |     2 |  -2.39015247655974 | -22.415793141205697 |   20.025640664609 |
| 2010 |     3 |   -5.2398807273353 | -31.351377159644244 |  26.1114964263708 |
| 2010 |     4 |   -8.8526688159346 | -29.596520316983344 | 20.74385052882399 |
| 2010 |     5 |   10.5262870405008 | -14.089292293581849 | 24.61560516398685 |
| 2010 |     6 |     8.989415526941 | -18.892246699206524 | 27.60214085447593 |
+------+-------+--------------------+---------------------+-------------------+

would produce the following signals
+------+-------+--------------------+-----------+-----------+------------+
| year | month | StockChange BHS    | (s-i) BHS | agg BHS   | aggBHS adj |
+------+-------+--------------------+-----------+-----------+------------+
| 2010 |     1 |  1                 |   1       | 2         |  1         |
| 2010 |     2 | -1                 |   1       | 0         |  0         |
| 2010 |     3 | -1                 |   1       | 0         |  0         |
| 2010 |     4 | -1                 |   1       | 0         |  0         |
| 2010 |     5 |  1                 |   1       | 2         |  1         |
| 2010 |     6 |  1                 |   1       | 2         |  1         |
+------+-------+--------------------+-----------+-----------+------------+
                                                | SUM       |  3         |
                                                +-----------+------------+

