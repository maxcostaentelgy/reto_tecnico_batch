# bch_retotecnico

Se incluyen los siguientes artefactos:
- Proyecto APX BATCH con Job Update registros de acuerdo a la fecha de entrada (YYYY-MM-DD) + 1 día:
    - Se toma como filtro los campos:
        - creation_date : se compara con la fecha de entrada + 1 día
        - process_status : se compara que sea igual a "TO_SEND"
    - Se actualizan los campos:
        - process_status : se setea el valor "SENT"
        - audit_date : se setea la fecha actual