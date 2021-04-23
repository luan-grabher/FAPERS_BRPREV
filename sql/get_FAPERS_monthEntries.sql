SELECT
BDCOMPL as historico,
BDDEBITO as debito,
BDCREDITO as credito,
BDVALOR as valor
FROM VSUC_EMPRESAS_TLAN L
WHERE
BDCODEMP = :enterprise
AND BDDATA >= ':year-:month-01' AND BDDATA <= ':year-:month-:lastDayOfMonth'