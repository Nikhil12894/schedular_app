import { Component, OnInit } from '@angular/core';
import { NodeService } from 'src/app/demo/service/node.service';
import { TreeNode} from 'primeng/api';

@Component({
    templateUrl: './treedemo.component.html'
})
export class TreeDemoComponent implements OnInit {

    files1: TreeNode<any>[] = [];

    files2: TreeNode<any>[] = [];

    files3: TreeNode<any>[] = [];

    selectedFiles1: TreeNode<any> | TreeNode<any>[] = [];

    selectedFiles2: TreeNode<any> | TreeNode<any>[] = [];

    selectedFiles3: TreeNode<any> | TreeNode<any>[] = {};

    cols: any[] = [];

    constructor(private nodeService: NodeService) {}

    ngOnInit() {
        this.nodeService.getFiles().then(files => this.files1 = files);
        this.nodeService.getFilesystem().then(files => this.files2 = files);
        this.nodeService.getFiles().then(files => {
            this.files3 = [{
                label: 'Root',
                children: files
            }];
        });

        this.cols = [
            { field: 'name', header: 'Name' },
            { field: 'size', header: 'Size' },
            { field: 'type', header: 'Type' }
        ];
    }
}
