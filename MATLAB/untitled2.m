

  %item kmeans hyrec
  Ciao=[1456 1643.2 3574.77]
  Caio= Ciao*(4+4);
    %item kmeans hyrec
  ml100=[12465.6 12582.2 16828.3]*8
   %item kmeans hyrec
  ml1m=[19789.2 19805.76 54475.8]*8
  D=[Ciao;ml100;ml1m]

figure
bar(D);
grid on;  
set(gca, 'xticklabel',{'Ciao','Ml100K','Ml1M'});
legend('Item','Kmeans','HyRec','Location','northwest');
xlabel('DtaSet');
ylabel('Bytes')
title('Storage in the Client');