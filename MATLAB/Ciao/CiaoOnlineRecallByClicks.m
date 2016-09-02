clear all
filename = '../Ciao/ciaoonline/CiaoItemsuserdistribution.txt';
delimiterIn = ' ';
A = importdata(filename,delimiterIn);
filename = '../Ciao/ciaoonline/CiaoKmeansuserdistribution.txt';
delimiterIn = ' ';
B = importdata(filename,delimiterIn);
filename = '../Ciao/ciaoonline/CiaoHyruserdistribution.txt';
delimiterIn = ' ';
C = importdata(filename,delimiterIn);
D=[A(1:15,3)';B(1:15,3)';C(1:15,3)']';
figure
bar(D);
axis([0 inf 0 1])
grid on;  
set(gca, 'xticklabel',{'0-4','4-8','8-12','12-16','16-20','20-24','24-28','28-32','32-36','36-40','40-44','44-48','48-52','52-56','>56'});
legend('Item','Kmeans','HyRec','Location','northwest');
xlabel('the Number of Clicks');
ylabel('KNN-Recall')
title('KNN-Recall Online');

