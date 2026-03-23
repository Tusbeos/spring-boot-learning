const fs = require('fs');
const path = require('path');

const DIR_TO_PROCESS = path.join(__dirname, 'src/main/java/com/emedicalbooking');

const idNames = [
    'id', 'doctorId', 'clinicId', 'specialtyId', 'packageId', 
    'positionId', 'roleId', 'userId', 'bookingId', 'patientId',
    'timeType', 'gender' // some enums are linked by ID or AllCode references
];

const regexIntVars = new RegExp(`\\bint\\s+(${idNames.join('|')})\\b`, 'g');
const regexIntegerVars = new RegExp(`\\bInteger\\s+(${idNames.join('|')})\\b`, 'g');

let changedFiles = 0;

function processFile(filePath) {
    let content = fs.readFileSync(filePath, 'utf8');
    let original = content;

    // 1. Types in declarations and signatures
    content = content.replace(regexIntVars, 'Long $1');
    content = content.replace(regexIntegerVars, 'Long $1');

    // 2. JpaRepository generic types
    content = content.replace(/JpaRepository<([a-zA-Z0-9_]+),\s*Integer>/g, 'JpaRepository<$1, Long>');
    
    // 3. Lists of IDs
    content = content.replace(/\bList<Integer>\b/g, 'List<Long>');
    content = content.replace(/\bArrayList<Integer>\b/g, 'ArrayList<Long>');
    
    // 4. Integer.parseInt / Integer.valueOf -> Long.parseLong / Long.valueOf
    content = content.replace(/\bInteger\.parseInt\((.*?)\)/g, 'Long.parseLong($1)');
    content = content.replace(/\bInteger\.valueOf\((.*?)\)/g, 'Long.valueOf($1)');

    if (content !== original) {
        fs.writeFileSync(filePath, content, 'utf8');
        console.log(`Updated: ${filePath}`);
        changedFiles++;
    }
}

function traverseDir(dir) {
    const files = fs.readdirSync(dir);
    for (const file of files) {
        const fullPath = path.join(dir, file);
        if (fs.statSync(fullPath).isDirectory()) {
            traverseDir(fullPath);
        } else if (fullPath.endsWith('.java')) {
            processFile(fullPath);
        }
    }
}

console.log('Starting refactoring...');
traverseDir(DIR_TO_PROCESS);
console.log(`Refactoring complete. Modified ${changedFiles} files.`);
