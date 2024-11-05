/**
 * Theme: Frogetor - Responsive Bootstrap 4 Admin Dashboard
 * Author: Mannatthemes
 * Footable Js
 */


$(function () {
    "use strict";

    /*Editing FooTable*/
    var $modal = $('#editor-modal'),
        $editor = $('#editor'),
        $editorTitle = $('#editor-title'),
        ft = FooTable.init('#footable-3', {
            editing: {
                enabled: true,
                addRow: function () {
                    $modal.removeData('row');

                    $editor.find('#color').attr('disabled', false);
                    $editor.find('#size').attr('disabled', false);
                    $editor.find('#color option').removeAttr('selected').change();
                    $editor.find('#size option').removeAttr('selected').change();
                    $editor[0].reset();

                    $editorTitle.text('Create a new product detail');

                    $modal.modal('show');
                },
                editRow: function (row) {
                    var values = row.val();
                    $editor.find('#id').val(values.id);

                    $editor.find('#color option').removeAttr('selected')
                        .filter(function(){ return $(this).text() === values.color;})
                        .attr('selected',true).change();
                    $editor.find('#color').attr('disabled', true);

                    $editor.find('#size option').removeAttr('selected')
                        .filter(function(){ return $(this).text() === values.size;})
                        .attr('selected',true).change();
                    $editor.find('#size').attr('disabled', true);

                    $editor.find('#stock').val(values.stock);

                    $modal.data('row', row);
                    $editorTitle.text('Edit row #' + values.id);
                    $modal.modal('show');
                },
                deleteRow: function (row) {
                    $.ajax({
                        type: "GET",
                        url: "/admin/product/product-detail/" + row.val().id + "/deactivate/check"
                    }).done(function (res){
                        swal({
                            title: 'Are you sure?',
                            text: "You won't be able to revert this!",
                            html : res.message,
                            type: 'warning',
                            showCancelButton: true,
                            confirmButtonText: 'Yes, delete it!',
                            cancelButtonText: 'No, cancel!',
                            confirmButtonClass: 'btn btn-success',
                            cancelButtonClass: 'btn btn-danger ml-2',
                            buttonsStyling: false
                        }).then((result) => {
                            if (result.value) {
                                // This function will run ONLY if the user clicked Yes!
                                // Only here we want to send the request to the server!
                                $.ajax({
                                    type: "Delete",
                                    url: "/admin/product/product-detail/" + row.val().id + "/delete",
                                }).done(function (res) {
                                    swal(
                                        'Deleted!',
                                        res.message,
                                        'success'
                                    )
                                    row.delete();
                                }).fail(function (e) {
                                    console.log(e);
                                    swal(
                                        'Not found!',
                                        e.responseJSON.message,
                                        'error'
                                    )
                                });
                            } else {
                                // This function will run if the user clicked "cancel"
                                swal(
                                    'Cancelled!',
                                    'Your product detail is safe :)',
                                    'error'
                                )
                            }
                        });
                    });
                }
            }
        }, function (ft){
            addEventToCheckbox();
        });

    $('#footable-3').on('after.ft.paging', function () {
        addEventToCheckbox();
    });

    $('#footable-3').on('after.ft.filtering', function () {
        addEventToCheckbox();
    });

    function addEventToCheckbox() {
        let $checkbox = $('#footable-3 input[type="checkbox"]');

        $checkbox.on('click', function (e) {
            e.preventDefault();

            let id = $(this).closest('tr').find('td:first').text();

            let $checkboxClicked = $(this);

            if (!this.checked){
                $.ajax({
                    type: "GET",
                    url: "/admin/product/product-detail/" + id + "/deactivate/check"
                }).done(function (res){
                    swal({
                        title: 'Are you sure you want to deactivate this product detail?',
                        html: res.message,
                        type: 'warning',
                        showCancelButton: true,
                        confirmButtonText: 'Yes, deactivate it!',
                        cancelButtonText: 'No, cancel!',
                        confirmButtonClass: 'btn btn-success',
                        cancelButtonClass: 'btn btn-danger ml-2',
                        buttonsStyling: false
                    }).then((result) => {
                        if (result.value) {
                            // This function will run ONLY if the user clicked Yes!
                            // Only here we want to send the request to the server!
                            $.ajax({
                                type: "PUT",
                                url: "/admin/product/product-detail/" + id + "/deactivate"
                            }).done(function (res){
                                swal(
                                    'Deactivate!',
                                    res.message,
                                    'success'
                                )
                                $checkboxClicked.prop('checked', false);
                            })
                        } else {
                            // This function will run if the user clicked "cancel"
                            swal(
                                'Cancelled!',
                                'Your product detail remain active :)',
                                'error'
                            )
                        }
                    });
                })
            }
            else{
                swal({
                    title: 'Are you sure?',
                    text: "This action will activate this product detail!",
                    type: 'warning',
                    showCancelButton: true,
                    confirmButtonText: 'Yes, activate it!',
                    cancelButtonText: 'No, cancel!',
                    confirmButtonClass: 'btn btn-success',
                    cancelButtonClass: 'btn btn-danger ml-2',
                    buttonsStyling: false
                }).then((result) => {
                    if (result.value) {
                        // This function will run ONLY if the user clicked Yes!
                        // Only here we want to send the request to the server!
                        $.ajax({
                            type: "PUT",
                            url: "/admin/product/product-detail/" + id + "/activate"
                        }).done(function (res){
                            swal(
                                'Activate!',
                                res.message,
                                'success'
                            )
                            $checkboxClicked.prop('checked', true);
                        })

                    } else {
                        // This function will run if the user clicked "cancel"
                        swal(
                            'Cancelled!',
                            'Your product detail remain inactive :)',
                            'error'
                        )
                    }
                });
            }
        })
    }

    $editor.on('submit', function (e) {
        if (this.checkValidity && !this.checkValidity()) return;
        e.preventDefault();

        $editor.find('#color').removeAttr('disabled');
        $editor.find('#size').removeAttr('disabled');

        var row = $modal.data('row'),
            values = {
                id: $editor.find('#id').val(),
                productId: $editor.find('#product').val(),
                colorId: $editor.find('#color').val(),
                sizeId: $editor.find('#size').val(),
                stock: $editor.find('#stock').val()
            };

        $.ajax({
            type: "POST",
            url: "/admin/product/product-detail/save",
            contentType: 'application/json',
            data: JSON.stringify(values),
        }).done(function (res) {
            if (row instanceof FooTable.Row) { //update case
                row.val(values);
            } else {  //insert case
                //loop through all row to check: if productDetail imported is already available row => calculate up stock
                var check = true;
                $.each(ft.rows.all, function(i, row){
                    var v = row.val();
                    if (v.id == res.data.id){
                        v.stock = res.data.stock;
                        check = false;
                        row.val(v);
                        return false;
                    }
                });
                //else add new product detail row
                if (check){
                    res.data.color = $editor.find('#color option:selected').text();
                    res.data.size = $editor.find('#size option:selected').text();
                    res.data.isActive = '<div class="checkbox checkbox-primary checkbox-single">\n' +
                        '                  <input type="checkbox" id="singleCheckbox2" value="option2" aria-label="Single checkbox Two" checked="checked">\n' +
                        '                  <label></label>\n' +
                        '                </div>';
                    ft.rows.add(res.data);
                }
            }
            swal({
                title: 'Success!',
                text: res.message,
                type: 'success',
                showCancelButton: false,
                confirmButtonClass: 'btn btn-success'
            });
        }).fail(function (error) {
            swal({
                type: "error",
                title: "Oops...",
                text: error.responseJSON.message,
            });
        });
        $modal.modal('hide');
    });

    var $modal2 = $('#editor-modal-2'),
        $editor2 = $('#editor-2'),
        $editorTitle2 = $('#editor-title-2'),
        ft2 = FooTable.init('#footable-3-2', {
            editing: {
                enabled: true,
                addRow: function () {
                    $modal.removeData('row');

                    $editor2.find('#color-2').attr('disabled', false);
                    $editor2.find('#color-2 option').removeAttr('selected').change();
                    $editor2[0].reset();
                    $editorTitle2.text('Import a new image');

                    $modal2.modal('show');
                },
                editRow: function (row) {
                    var values = row.val();
                    $editor2.find('#id-2').val(values.id);

                    $editor2.find('#color-2 option').removeAttr('selected')
                        .filter(function(){ return $(this).text() === values.color;})
                        .attr('selected',true).change();
                    $editor2.find('#color-2').attr('disabled', true);

                    $modal2.data('row', row);
                    $editorTitle2.text('Edit row #' + values.id);
                    $modal2.modal('show');
                },
                deleteRow: function (row) {
                    $.ajax({
                        type: "GET",
                        url: "/admin/product/product-image/" + row.val().id + "/deactivate/check"
                    }).done(function (res){
                        swal({
                            title: 'Are you sure?',
                            text: "You won't be able to revert this!",
                            html: res.message,
                            type: 'warning',
                            showCancelButton: true,
                            confirmButtonText: 'Yes, delete it!',
                            cancelButtonText: 'No, cancel!',
                            confirmButtonClass: 'btn btn-success',
                            cancelButtonClass: 'btn btn-danger ml-2',
                            buttonsStyling: false
                        }).then((result) => {
                            if (result.value) {
                                // This function will run ONLY if the user clicked Yes!
                                // Only here we want to send the request to the server!
                                $.ajax({
                                    type: "Delete",
                                    url: "/admin/product/product-image/" + row.val().id + "/delete",
                                }).done(function (res) {
                                    swal(
                                        'Deleted!',
                                        res.message,
                                        'success'
                                    )
                                    row.delete();
                                }).fail(function (e) {
                                    console.log(e);
                                    swal(
                                        'Not found!',
                                        e.responseJSON.message,
                                        'error'
                                    )
                                });
                            } else {
                                // This function will run if the user clicked "cancel"
                                swal(
                                    'Cancelled!',
                                    'Your product image is safe :)',
                                    'error'
                                )
                            }
                        });
                    });
                }
            }
        }, function (){
            addEventToCheckbox2();
        });

    $('#footable-3-2').on('after.ft.paging', function () {
        addEventToCheckbox2();
    });

    $('#footable-3-2').on('after.ft.filtering', function () {
        addEventToCheckbox2();
    });

    function addEventToCheckbox2() {
        let $checkbox = $('#footable-3-2 input[type="checkbox"]');

        $checkbox.on('click', function (e) {
            e.preventDefault();

            let id = $(this).closest('tr').find('td:first').text();

            let $checkboxClicked = $(this);

            if (!this.checked){
                $.ajax({
                    type: "GET",
                    url: "/admin/product/product-image/" + id + "/deactivate/check"
                }).done(function (res){
                    swal({
                        title: 'Are you sure you want to deactivate this product image?',
                        html: res.message,
                        type: 'warning',
                        showCancelButton: true,
                        confirmButtonText: 'Yes, deactivate it!',
                        cancelButtonText: 'No, cancel!',
                        confirmButtonClass: 'btn btn-success',
                        cancelButtonClass: 'btn btn-danger ml-2',
                        buttonsStyling: false
                    }).then((result) => {
                        if (result.value) {
                            // This function will run ONLY if the user clicked Yes!
                            // Only here we want to send the request to the server!
                            $.ajax({
                                type: "PUT",
                                url: "/admin/product/product-image/" + id + "/deactivate"
                            }).done(function (res){
                                swal(
                                    'Deactivate!',
                                    res.message,
                                    'success'
                                )
                                $checkboxClicked.prop('checked', false);
                            })
                        } else {
                            // This function will run if the user clicked "cancel"
                            swal(
                                'Cancelled!',
                                'Your product detail remain active :)',
                                'error'
                            )
                        }
                    });
                })
            }
            else{
                swal({
                    title: 'Are you sure?',
                    text: "This action will activate this product image!",
                    type: 'warning',
                    showCancelButton: true,
                    confirmButtonText: 'Yes, activate it!',
                    cancelButtonText: 'No, cancel!',
                    confirmButtonClass: 'btn btn-success',
                    cancelButtonClass: 'btn btn-danger ml-2',
                    buttonsStyling: false
                }).then((result) => {
                    if (result.value) {
                        // This function will run ONLY if the user clicked Yes!
                        // Only here we want to send the request to the server!
                        $.ajax({
                            type: "PUT",
                            url: "/admin/product/product-image/" + id + "/activate"
                        }).done(function (res){
                            swal(
                                'Activate!',
                                res.message,
                                'success'
                            )
                            $checkboxClicked.prop('checked', true);
                        })

                    } else {
                        // This function will run if the user clicked "cancel"
                        swal(
                            'Cancelled!',
                            'Your product detail remain inactive :)',
                            'error'
                        )
                    }
                });
            }
        })
    }

    $editor2.on('submit', function (e) {
        if (this.checkValidity && !this.checkValidity()) return;
        e.preventDefault();

        $editor.find('#color-2').removeAttr('disabled');

        var row = $modal2.data('row');
        var values = new FormData();
        values.append('id', $editor2.find('#id-2').val());
        values.append('productId', $editor2.find('#product-2').val());
        values.append('colorId', Number($editor2.find('#color-2').val()));
        values.append('fileImage', $editor2.find('#image')[0].files[0]);

        $.ajax({
            type: "POST",
            url: "/admin/product/product-image/save",
            data: values,
            processData: false,
            contentType: false
        }).done(function (res) {
            if (row instanceof FooTable.Row) { //update case
                values = {
                    image : '<img class="d-block img-fluid" src="/file/download/' + res.data.name + '" alt="First slide">'
                };
                row.val(values);
            } else {  //insert case
                res.data.color = $editor2.find('#color-2 option:selected').text();
                res.data.image = '<img class="d-block img-fluid" src="/file/download/' + res.data.name + '" alt="First slide">';
                res.data.isActive = '<div class="checkbox checkbox-primary checkbox-single">\n' +
                    '                  <input type="checkbox" id="singleCheckbox2" value="option2" aria-label="Single checkbox Two" checked="checked">\n' +
                    '                  <label></label>\n' +
                    '                </div>';
                ft2.rows.add(res.data);
            }
            swal({
                title: 'Success!',
                text: res.message,
                type: 'success',
                showCancelButton: false,
                confirmButtonClass: 'btn btn-success'
            });
        }).fail(function (error) {
            swal({
                type: "error",
                title: "Oops...",
                text: error.responseJSON.message,
            });
        });
        $modal.modal('hide');
    });

});