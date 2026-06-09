$page1Text = @"
BT
/F1 18 Tf
80 720 Td
(Data Flow Diagram Level 0) Tj
ET
BT
/F1 12 Tf
60 680 Td
(Policyholder, Approver, Admin) Tj
ET
BT
/F1 10 Tf
70 620 Td
(Policyholder) Tj
ET
120 625 m 220 625 l S
BT
/F1 10 Tf
400 620 Td
(Approver) Tj
ET
450 625 m 550 625 l S
BT
/F1 10 Tf
70 500 Td
(Admin) Tj
ET
120 505 m 220 505 l S
BT
/F1 12 Tf
240 520 Td
(Claim System) Tj
ET
260 510 m 360 510 l 360 560 l 260 560 l h S
BT
/F1 10 Tf
270 540 Td
(Insurance Claim) Tj
ET
BT
/F1 10 Tf
270 525 Td
(Management) Tj
ET
260 450 m 260 430 l S
180 470 m 260 470 l S
360 470 m 440 470 l S
200 505 m 200 520 l S
340 505 m 340 520 l S
BT
/F1 10 Tf
270 400 Td
(Database: health_db) Tj
ET
150 380 m 450 380 l 450 420 l 150 420 l h S
"@

$page2Text = @"
BT
/F1 18 Tf
80 720 Td
(Data Flow Diagram Level 1) Tj
ET
BT
/F1 12 Tf
50 680 Td
(Policyholder) Tj
ET
BT
/F1 10 Tf
70 650 Td
(Login / Profile / Claim Submission) Tj
ET
150 660 m 330 660 l 330 710 l 150 710 l h S
BT
/F1 10 Tf
190 685 Td
(Process Claims) Tj
ET
BT
/F1 12 Tf
390 680 Td
(Approver) Tj
ET
410 650 m 510 650 l 510 690 l 410 690 l h S
BT
/F1 10 Tf
420 670 Td
(Review / Approve) Tj
ET
BT
/F1 12 Tf
50 520 Td
(Admin) Tj
ET
70 490 m 220 490 l 220 540 l 70 540 l h S
BT
/F1 10 Tf
90 515 Td
(Manage Users / Policies) Tj
ET
BT
/F1 12 Tf
270 530 Td
(Data Stores) Tj
ET
BT
/F1 10 Tf
260 450 Td
(users, policies, claims, documents, payments) Tj
ET
180 460 m 530 460 l 530 500 l 180 500 l h S
BT
/F1 10 Tf
200 580 Td
(Submit Claim) Tj
ET
BT
/F1 10 Tf
420 580 Td
(Approve / Reject) Tj
ET
BT
/F1 10 Tf
120 540 Td
(Assign Policies) Tj
ET
BT
/F1 10 Tf
280 510 Td
(Read/Write Data) Tj
ET
"@

function AddObject($id, $content) {
    return "${id} 0 obj\r\n$content\r\nendobj\r\n"
}

$objects = @()
$objects += @{id=1; content='<< /Type /Catalog /Pages 2 0 R >>'}
$objects += @{id=2; content='<< /Type /Pages /Kids [3 0 R 6 0 R] /Count 2 >>'}
$objects += @{id=3; content='<< /Type /Page /Parent 2 0 R /MediaBox [0 0 612 792] /Contents 7 0 R /Resources << /Font << /F1 4 0 R >> >> >>'}
$objects += @{id=4; content='<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>'}
$objects += @{id=5; content='<< /Type /Page /Parent 2 0 R /MediaBox [0 0 612 792] /Contents 8 0 R /Resources << /Font << /F1 4 0 R >> >> >>'}

$page1Stream = "<< /Length {0} >>\r\nstream\r\n{1}endstream" -f ([Text.Encoding]::ASCII.GetByteCount($page1Text)), $page1Text
$page2Stream = "<< /Length {0} >>\r\nstream\r\n{1}endstream" -f ([Text.Encoding]::ASCII.GetByteCount($page2Text)), $page2Text
$objects += @{id=7; content=$page1Stream}
$objects += @{id=8; content=$page2Stream}

$path = Join-Path (Get-Location) 'dfd.pdf'
$ms = New-Object System.IO.MemoryStream
function WriteBytes($bytes) {
    $ms.Write($bytes, 0, $bytes.Length)
}

$headerBytes = [Text.Encoding]::ASCII.GetBytes('%PDF-1.4\r\n')
WriteBytes $headerBytes
$currentOffset = $headerBytes.Length
$xrefLines = @()

foreach ($obj in $objects) {
    $text = AddObject $obj.id $obj.content
    $bytes = [Text.Encoding]::ASCII.GetBytes($text)
    $xrefLines += '{0:D10} 00000 n ' -f $currentOffset
    WriteBytes $bytes
    $currentOffset += $bytes.Length
}

$xrefStart = $currentOffset
$xrefHeader = "xref\r\n0 {0}\r\n0000000000 65535 f \r\n" -f ($objects.Count + 1)
WriteBytes ([Text.Encoding]::ASCII.GetBytes($xrefHeader))
$currentOffset += [Text.Encoding]::ASCII.GetByteCount($xrefHeader)
foreach ($line in $xrefLines) {
    WriteBytes ([Text.Encoding]::ASCII.GetBytes($line + "\r\n"))
    $currentOffset += [Text.Encoding]::ASCII.GetByteCount($line + "\r\n")
}

$trailer = "trailer << /Size {0} /Root 1 0 R >>\r\nstartxref\r\n{1}\r\n%%EOF\r\n" -f ($objects.Count + 1), $xrefStart
WriteBytes ([Text.Encoding]::ASCII.GetBytes($trailer))

[System.IO.File]::WriteAllBytes($path, $ms.ToArray())