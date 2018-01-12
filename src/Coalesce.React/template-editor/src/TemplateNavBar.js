import React, { Component } from 'react';
import styles from './sideBarMenu.css';
import SideNav, { Nav, NavIcon, NavText } from 'react-sidenav';
import { ic_aspect_ratio } from 'react-icons-kit/md/ic_aspect_ratio';
import { ic_business } from 'react-icons-kit/md/ic_business';
import SvgIcon from 'react-icons-kit';
import { plus } from 'react-icons-kit/icomoon/plus';
import { enter } from 'react-icons-kit/icomoon/enter';
import { floppyDisk } from 'react-icons-kit/icomoon/floppyDisk';
import { cross } from 'react-icons-kit/icomoon/cross';
import { stack } from 'react-icons-kit/icomoon/stack';
import { statsDots } from 'react-icons-kit/icomoon/statsDots';
import { fileText } from 'react-icons-kit/icomoon/fileText';
import { exit } from 'react-icons-kit/icomoon/exit';
import Icon from 'react-icons-kit';
import styled from 'styled-components';
import TestModal from './TestModal';

class TemplateNavBar extends Component {
    constructor(props) {
        super(props);
        this.handleTemplateAdd = this.handleTemplateAdd.bind(this);
        this.handleGraphAdd = this.handleGraphAdd.bind(this);
        this.handleFreeze = this.handleFreeze.bind(this);
    }

    handleTemplateAdd(e) {
        console.log("adding template to workspace...triggered");
        this.props.onTemplateAdd();
    }

    handleGraphAdd(e) {
        this.props.onGraphAdd();
    }

    handleFreeze(e) {
        console.log("freeze...triggered");
        this.props.onFreeze();
    }

    render() {

        const SeparatorTitleContainer = styled.div`
        font-size: 14px;
        color: #AAA;
        margin: 5px 6px;
        padding: 4px 12px 2px;
    `;

        const SeparatorTitle = props => {
            return (
                <SeparatorTitleContainer>
                    {props.children}
                    <hr style={{ border: 0, borderTop: '1px solid #E5E5E5' }} />
                </SeparatorTitleContainer>
            );
        };

       // <Nav id='templates1'>
     //   <NavIcon><SvgIcon size={20} icon={ic_business} /></NavIcon>
   //     <NavText> <Icon icon={fileText} /> test</NavText>
 //   </Nav>

        return (
            <div style={{ background: '#4f4f4f', color: '#dedede', width: 220 }}>
                <SideNav highlightColor='#000000' highlightBgColor='#dedede' >
                    <Nav id='dashboard'>
                        <NavIcon><SvgIcon size={20} icon={ic_aspect_ratio} /></NavIcon>
                        <NavText> Dashboard </NavText>
                    </Nav>
                    <SeparatorTitle>
                        <div>Templates</div>
                    </SeparatorTitle>
                    <Nav id='templates'>
                        <NavIcon><Icon icon={stack} /></NavIcon>
                        <NavText>Templates</NavText>
   
                        <Nav id='templates2'>
                            <NavText> New Template </NavText>
                        </Nav>
                        <Nav id='templates2'>
                            <NavText> New Template 2</NavText>
                        </Nav>
                        <Nav id='templates2'>
                            <NavText> New Template 3</NavText>
                        </Nav>
                    </Nav>
                    <Nav id='templatesgraph' onNavClick={this.handleGraphAdd}>
                        <NavIcon><Icon icon={statsDots} /></NavIcon>
                        <NavText>Template Graph</NavText>
                    </Nav>
                    <Nav id='templatesnew' onNavClick={this.handleTemplateAdd}>
                        <NavIcon><Icon icon={plus} /></NavIcon>
                        <NavText>New Template</NavText>
                    </Nav>
                    <Nav id='templatesload' >
                        <NavIcon><Icon icon={enter} /></NavIcon>
                        <NavText>Load Template</NavText>
                    </Nav>
                    <Nav id='templatessave'>
                        <NavIcon><Icon icon={floppyDisk} /></NavIcon>
                        <NavText>Save Template</NavText>
                    </Nav>
                    <Nav id='templatesdelete'>
                        <NavIcon><Icon icon={cross} /></NavIcon>
                        <NavText>Delete Templates</NavText>
                    </Nav>
                    <SeparatorTitle>
                        <div>Export</div>
                    </SeparatorTitle>
                    <Nav id='codegen'>
                        <NavIcon><Icon icon={exit} /></NavIcon>
                        <NavText>Generate Code</NavText>
                        <Nav id='java'>
                            <NavText> To Java</NavText>
                        </Nav>
                    </Nav>
                </SideNav>
            </div>

        );

    }
}

export default TemplateNavBar;
