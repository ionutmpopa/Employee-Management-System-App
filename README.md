Aplicatie pontaj : angajatii ponteaza timp pe proiecte ca sa se poata calcula costul proiectelor

- 1 angajat poate vedea date lui personale 
- management timecards (CRUD)
- calcul cost per proiect: 15 euro/junior, 30 - medium, 50 - senior 
- detalii proiect: cine a lucrat pe proiect, cost final


Model:
Employee: ce exista deja + title devine enum
Timecard: id, date, comment, hours, employee, project
Project: id, name, description
Costs: title - ar trebui sa fie enum, cost


tabele:
employee 
costs (id, job title & price)
projects (id, name, description)
timecards (id, employee_id, project_id, date, comment, hours)


- creare crud-uri pentru entitati noi cu validari
- adaugare timecard nou : max 8 ore, daca exista deja pe proiectul 1 in ziua x si se face adaugare noua, update nu insert
- procesare date: o persoana la cate proiecte a lucrat, nr ore lucrate pe proiect, cost total, proiect
- unit teste
