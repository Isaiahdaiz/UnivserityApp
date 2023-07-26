CREATE TABLE IF NOT EXISTS students (
	id INT(9) NOT NULL PRIMARY KEY,
	name TEXT(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS courses (
	crn TEXT(10) NOT NULL PRIMARY KEY,
	title TEXT(200) NOT NULL
);

CREATE TABLE IF NOT EXISTS enrolment (
	student INT(9) NOT NULL,
	crn TEXT(10) NOT NULL,
	FOREIGN KEY(student) REFERENCES students(id),
	FOREIGN KEY(crn) REFERENCES courses(crn),
	PRIMARY KEY (student, crn)
);

CREATE TABLE IF NOT EXISTS grades (
	student INT(9) NOT NULL, 
	crn TEXT(10) NOT NULL,
	grade INT NOT NULL,
	FOREIGN KEY(student) REFERENCES students(id),
	FOREIGN KEY(crn) REFERENCES courses(crn),
	PRIMARY KEY (student, crn)
);

INSERT OR IGNORE INTO courses (crn, title) VALUES ("CS359623SP", "COSC 3596: Mobile Application Development I");
INSERT OR IGNORE INTO courses (crn, title) VALUES ("CS200623SS", "COSC 2006: Data Structures I");
INSERT OR IGNORE INTO courses (crn, title) VALUES ("CS240623SS", "COSC 2406: Assembly Languae Programming");

INSERT OR IGNORE INTO students (id, name) VALUES (123456789, "Test Student 1");
INSERT OR IGNORE INTO students (id, name) VALUES (234567890, "Test Student 2");
INSERT OR IGNORE INTO students (id, name) VALUES (345678901, "Test Student 3");

INSERT OR IGNORE INTO enrolment (student, crn) VALUES (123456789, "CS359623SP");
INSERT OR IGNORE INTO enrolment (student, crn) VALUES (234567890, "CS200623SS");