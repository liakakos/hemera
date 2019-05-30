import { DamlLfValue } from '@da/ui-core';

export const version = {
  schema: 'navigator-config',
  major: 2,
  minor: 0,
};

export const customViews = (userId, party, role) => ({
  hemera_operator: {
    type: "table-view",
    title: "Hemera Operator",
    source: {
      type: "contracts",
      filter: [
        {
          field: "argument.operator",
          value: party,
        },
        {
          field: "template.id",
          value: "Ethereum.Onboarding:Operator"
        }
      ],
      search: "",
      sort: [
        {
          field: "id",
          direction: "ASCENDING"
        }
      ]
    },
    columns: [
      {
        key: "argument.operator",
        title: "Operator",
        createCell: ({rowData}) => ({
          type: "text",
          value: DamlLfValue.toJSON(rowData.argument).operator
        }),
        sortable: true,
        width: 80,
        weight: 0,
        alignment: "left"
      }
    ]
  },
  hemera_user: {
    type: "table-view",
    title: "Hemera User",
    source: {
      type: "contracts",
      filter: [
        {
          field: "template.id",
          value: "Ethereum.Onboarding:User"
        }
      ],
      search: "",
      sort: [
        {
          field: "id",
          direction: "ASCENDING"
        }
      ]
    },
    columns: [
      {
        key: "argument.user",
        title: "User",
        createCell: ({rowData}) => ({
          type: "text",
          value: DamlLfValue.toJSON(rowData.argument).user
        }),
        sortable: true,
        width: 80,
        weight: 0,
        alignment: "left"
      },
      {
        key: "argument.operator",
        title: "Operator",
        createCell: ({rowData}) => ({
          type: "text",
          value: DamlLfValue.toJSON(rowData.argument).operator
        }),
        sortable: true,
        width: 80,
        weight: 0,
        alignment: "left"
      }
    ]
  },
  erc20_contracts: {
    type: "table-view",
    title: "ERC-20 Contracts",
    source: {
      type: "contracts",
      filter: [
        {
          field: "template.id",
          value: "Ethereum.ERC20Contract:ERC20Contract"
        }
      ],
      search: "",
      sort: [
        {
          field: "id",
          direction: "ASCENDING"
        }
      ]
    },
    columns: [
      {
        key: "argument.name",
        title: "Name",
        createCell: ({rowData}) => ({
          type: "text",
          value: DamlLfValue.toJSON(rowData.argument).name
        }),
        sortable: true,
        width: 80,
        weight: 0,
        alignment: "left"
      },
      {
        key: "argument.symbol",
        title: "Symbol",
        createCell: ({rowData}) => ({
          type: "text",
          value: DamlLfValue.toJSON(rowData.argument).symbol
        }),
        sortable: true,
        width: 80,
        weight: 0,
        alignment: "left"
      },
      {
        key: "argument.decimals",
        title: "Decimals",
        createCell: ({rowData}) => ({
          type: "text",
          value: DamlLfValue.toJSON(rowData.argument).decimals
        }),
        sortable: true,
        width: 80,
        weight: 0,
        alignment: "left"
      },
      {
        key: "argument.address",
        title: "Address",
        createCell: ({rowData}) => ({
          type: "text",
          value: DamlLfValue.toJSON(rowData.argument).address
        }),
        sortable: true,
        width: 260,
        weight: 0,
        alignment: "left"
      },
      {
        key: "argument.user",
        title: "User",
        createCell: ({rowData}) => ({
          type: "text",
          value: DamlLfValue.toJSON(rowData.argument).user
        }),
        sortable: true,
        width: 80,
        weight: 0,
        alignment: "left"
      },
      {
        key: "argument.operator",
        title: "Operator",
        createCell: ({rowData}) => ({
          type: "text",
          value: DamlLfValue.toJSON(rowData.argument).operator
        }),
        sortable: true,
        width: 80,
        weight: 0,
        alignment: "left"
      }
    ]
  },
  calls: {
    type: "table-view",
    title: "Calls",
    source: {
      type: "contracts",
      filter: [
        {
          field: "template.id",
          value: "Ethereum.Call:Call"
        }
      ],
      search: "",
      sort: [
        {
          field: "id",
          direction: "ASCENDING"
        }
      ]
    },
    columns: [
      {
        key: "template.id",
        title: "Type",
        createCell: ({rowData}) => ({
          type: "text",
          value: DamlLfValue.toJSON(rowData.argument).sendStatus ? "Response" : "Request"
        }),
        sortable: true,
        width: 80,
        weight: 0,
        alignment: "left"
      },
      {
        key: "argument.name",
        title: "Function Name",
        createCell: ({rowData}) => ({
          type: "text",
          value: DamlLfValue.toJSON(rowData.argument).name
        }),
        sortable: true,
        width: 80,
        weight: 0,
        alignment: "left"
      },
      {
        key: "argument.to",
        title: "To Contract",
        createCell: ({rowData}) => ({
          type: "text",
          value: DamlLfValue.toJSON(rowData.argument).to
        }),
        sortable: true,
        width: 260,
        weight: 0,
        alignment: "left"
      },
      {
        key: "argument.args",
        title: "Args",
        createCell: ({rowData}) => ({
          type: "text",
          value: JSON.stringify(DamlLfValue.toJSON(rowData.argument).args)
        }),
        sortable: true,
        width: 80,
        weight: 0,
        alignment: "left"
      },
      {
        key: "argument.response",
        title: "Response",
        createCell: ({rowData}) => ({
          type: "text",
          value: JSON.stringify(DamlLfValue.toJSON(rowData.argument).response)
        }),
        sortable: true,
        width: 80,
        weight: 0,
        alignment: "left"
      },
      {
        key: "argument.sendStatusWhen",
        title: "Sent",
        createCell: ({rowData}) => ({
          type: "text",
          value: DamlLfValue.toJSON(rowData.argument).sendStatus ? DamlLfValue.toJSON(rowData.argument).sendStatus["Sent"]["when"] : ""
        }),
        sortable: true,
        width: 80,
        weight: 0,
        alignment: "left"
      },
      {
        key: "argument.sendStatusFrom",
        title: "Client",
        createCell: ({rowData}) => ({
          type: "text",
          value: DamlLfValue.toJSON(rowData.argument).sendStatus ? DamlLfValue.toJSON(rowData.argument).sendStatus["Sent"]["from"] : ""
        }),
        sortable: true,
        width: 80,
        weight: 0,
        alignment: "left"
      },
      {
        key: "argument.user",
        title: "User",
        createCell: ({rowData}) => ({
          type: "text",
          value: DamlLfValue.toJSON(rowData.argument).user
        }),
        sortable: true,
        width: 80,
        weight: 0,
        alignment: "left"
      },
      {
        key: "argument.operator",
        title: "Operator",
        createCell: ({rowData}) => ({
          type: "text",
          value: DamlLfValue.toJSON(rowData.argument).operator
        }),
        sortable: true,
        width: 80,
        weight: 0,
        alignment: "left"
      }
    ]
  },
  transactions: {
    type: "table-view",
    title: "Transactions",
    source: {
      type: "contracts",
      filter: [
        {
          field: "template.id",
          value: "Ethereum.Transaction:"
        }
      ],
      search: "",
      sort: [
        {
          field: "id",
          direction: "ASCENDING"
        }
      ]
    },
    columns: [
      {
        key: "template.id",
        title: "Type",
        createCell: ({rowData}) => ({
          type: "text",
          value: DamlLfValue.toJSON(rowData.argument).sendStatus ? "Signed" : DamlLfValue.toJSON(rowData.argument).txToSign ? "Unsigned" : "Request"
        }),
        sortable: true,
        width: 80,
        weight: 0,
        alignment: "left"
      },
      {
        key: "argument.name",
        title: "Function Name",
        createCell: ({rowData}) => ({
          type: "text",
          value: DamlLfValue.toJSON(rowData.argument).name
        }),
        sortable: true,
        width: 80,
        weight: 0,
        alignment: "left"
      },
      {
        key: "argument.from",
        title: "From",
        createCell: ({rowData}) => ({
          type: "text",
          value: DamlLfValue.toJSON(rowData.argument).from
        }),
        sortable: true,
        width: 260,
        weight: 0,
        alignment: "left"
      },
      {
        key: "argument.to",
        title: "To Contract",
        createCell: ({rowData}) => ({
          type: "text",
          value: DamlLfValue.toJSON(rowData.argument).to
        }),
        sortable: true,
        width: 260,
        weight: 0,
        alignment: "left"
      },
      {
        key: "argument.args",
        title: "Args",
        createCell: ({rowData}) => ({
          type: "text",
          value: JSON.stringify(DamlLfValue.toJSON(rowData.argument).args)
        }),
        sortable: true,
        width: 80,
        weight: 0,
        alignment: "left"
      },
      {
        key: "argument.gas",
        title: "Gas",
        createCell: ({rowData}) => ({
          type: "text",
          value: DamlLfValue.toJSON(rowData.argument).gas
        }),
        sortable: true,
        width: 80,
        weight: 0,
        alignment: "left"
      },
      {
        key: "argument.gasPrice",
        title: "Gas Price",
        createCell: ({rowData}) => ({
          type: "text",
          value: JSON.stringify(DamlLfValue.toJSON(rowData.argument).gasPrice)
        }),
        sortable: true,
        width: 80,
        weight: 0,
        alignment: "left"
      },
      {
        key: "argument.nonce",
        title: "Nonce",
        createCell: ({rowData}) => ({
          type: "text",
          value: DamlLfValue.toJSON(rowData.argument).nonce
        }),
        sortable: true,
        width: 80,
        weight: 0,
        alignment: "left"
      },
      {
        key: "argument.sendStatus",
        title: "Status",
        createCell: ({rowData}) => ({
          type: "text",
          value: DamlLfValue.toJSON(rowData.argument).sendStatus ? JSON.stringify(DamlLfValue.toJSON(rowData.argument).sendStatus) : ""
        }),
        sortable: true,
        width: 80,
        weight: 0,
        alignment: "left"
      },
      {
        key: "argument.user",
        title: "User",
        createCell: ({rowData}) => ({
          type: "text",
          value: DamlLfValue.toJSON(rowData.argument).user
        }),
        sortable: true,
        width: 80,
        weight: 0,
        alignment: "left"
      },
      {
        key: "argument.operator",
        title: "Operator",
        createCell: ({rowData}) => ({
          type: "text",
          value: DamlLfValue.toJSON(rowData.argument).operator
        }),
        sortable: true,
        width: 80,
        weight: 0,
        alignment: "left"
      }
    ]
  },
  transfers: {
    type: "table-view",
    title: "Ether Transfers",
    source: {
      type: "contracts",
      filter: [
        {
          field: "template.id",
          value: "Ethereum.Transfer:"
        }
      ],
      search: "",
      sort: [
        {
          field: "id",
          direction: "ASCENDING"
        }
      ]
    },
    columns: [
      {
        key: "template.id",
        title: "Type",
        createCell: ({rowData}) => ({
          type: "text",
          value: DamlLfValue.toJSON(rowData.argument).sendStatus ? "Signed" : DamlLfValue.toJSON(rowData.argument).txToSign ? "Unsigned" : "Request"
        }),
        sortable: true,
        width: 80,
        weight: 0,
        alignment: "left"
      },
      {
        key: "argument.from",
        title: "From",
        createCell: ({rowData}) => ({
          type: "text",
          value: DamlLfValue.toJSON(rowData.argument).from
        }),
        sortable: true,
        width: 260,
        weight: 0,
        alignment: "left"
      },
      {
        key: "argument.to",
        title: "To",
        createCell: ({rowData}) => ({
          type: "text",
          value: DamlLfValue.toJSON(rowData.argument).to
        }),
        sortable: true,
        width: 260,
        weight: 0,
        alignment: "left"
      },
      {
        key: "argument.value",
        title: "Value",
        createCell: ({rowData}) => ({
          type: "text",
          value: JSON.stringify(DamlLfValue.toJSON(rowData.argument).value)
        }),
        sortable: true,
        width: 260,
        weight: 0,
        alignment: "left"
      },
      {
        key: "argument.gas",
        title: "Gas",
        createCell: ({rowData}) => ({
          type: "text",
          value: DamlLfValue.toJSON(rowData.argument).gas
        }),
        sortable: true,
        width: 80,
        weight: 0,
        alignment: "left"
      },
      {
        key: "argument.gasPrice",
        title: "Gas Price",
        createCell: ({rowData}) => ({
          type: "text",
          value: JSON.stringify(DamlLfValue.toJSON(rowData.argument).gasPrice)
        }),
        sortable: true,
        width: 80,
        weight: 0,
        alignment: "left"
      },
      {
        key: "argument.nonce",
        title: "Nonce",
        createCell: ({rowData}) => ({
          type: "text",
          value: DamlLfValue.toJSON(rowData.argument).nonce
        }),
        sortable: true,
        width: 80,
        weight: 0,
        alignment: "left"
      },
      {
        key: "argument.sendStatus",
        title: "Status",
        createCell: ({rowData}) => ({
          type: "text",
          value: DamlLfValue.toJSON(rowData.argument).sendStatus ? JSON.stringify(DamlLfValue.toJSON(rowData.argument).sendStatus) : ""
        }),
        sortable: true,
        width: 80,
        weight: 0,
        alignment: "left"
      },
      {
        key: "argument.user",
        title: "User",
        createCell: ({rowData}) => ({
          type: "text",
          value: DamlLfValue.toJSON(rowData.argument).user
        }),
        sortable: true,
        width: 80,
        weight: 0,
        alignment: "left"
      },
      {
        key: "argument.operator",
        title: "Operator",
        createCell: ({rowData}) => ({
          type: "text",
          value: DamlLfValue.toJSON(rowData.argument).operator
        }),
        sortable: true,
        width: 80,
        weight: 0,
        alignment: "left"
      }
    ]
  },
  smart_contracts: {
    type: "table-view",
    title: "Smart Contracts",
    source: {
      type: "contracts",
      filter: [
        {
          field: "template.id",
          value: "Ethereum.SmartContract:"
        }
      ],
      search: "",
      sort: [
        {
          field: "id",
          direction: "ASCENDING"
        }
      ]
    },
    columns: [
      {
        key: "template.id",
        title: "Type",
        createCell: ({rowData}) => ({
          type: "text",
          value: DamlLfValue.toJSON(rowData.argument).sendStatus ? "Signed" : DamlLfValue.toJSON(rowData.argument).txToSign ? "Unsigned" : "Request"
        }),
        sortable: true,
        width: 80,
        weight: 0,
        alignment: "left"
      },
      {
        key: "argument.name",
        title: "Contract Name",
        createCell: ({rowData}) => ({
          type: "text",
          value: DamlLfValue.toJSON(rowData.argument).name
        }),
        sortable: true,
        width: 80,
        weight: 0,
        alignment: "left"
      },
      {
        key: "argument.from",
        title: "From",
        createCell: ({rowData}) => ({
          type: "text",
          value: DamlLfValue.toJSON(rowData.argument).from
        }),
        sortable: true,
        width: 260,
        weight: 0,
        alignment: "left"
      },
      {
        key: "argument.ctorArgs",
        title: "Constructor Args",
        createCell: ({rowData}) => ({
          type: "text",
          value: JSON.stringify(DamlLfValue.toJSON(rowData.argument).ctorArgs)
        }),
        sortable: true,
        width: 80,
        weight: 0,
        alignment: "left"
      },
      {
        key: "argument.gas",
        title: "Gas",
        createCell: ({rowData}) => ({
          type: "text",
          value: DamlLfValue.toJSON(rowData.argument).gas
        }),
        sortable: true,
        width: 80,
        weight: 0,
        alignment: "left"
      },
      {
        key: "argument.gasPrice",
        title: "Gas Price",
        createCell: ({rowData}) => ({
          type: "text",
          value: JSON.stringify(DamlLfValue.toJSON(rowData.argument).gasPrice)
        }),
        sortable: true,
        width: 80,
        weight: 0,
        alignment: "left"
      },
      {
        key: "argument.value",
        title: "Initial Value",
        createCell: ({rowData}) => ({
          type: "text",
          value: JSON.stringify(DamlLfValue.toJSON(rowData.argument).value)
        }),
        sortable: true,
        width: 80,
        weight: 0,
        alignment: "left"
      },
      {
        key: "argument.nonce",
        title: "Nonce",
        createCell: ({rowData}) => ({
          type: "text",
          value: DamlLfValue.toJSON(rowData.argument).nonce
        }),
        sortable: true,
        width: 80,
        weight: 0,
        alignment: "left"
      },
      {
        key: "argument.sendStatus",
        title: "Status",
        createCell: ({rowData}) => ({
          type: "text",
          value: DamlLfValue.toJSON(rowData.argument).sendStatus ? JSON.stringify(DamlLfValue.toJSON(rowData.argument).sendStatus) : ""
        }),
        sortable: true,
        width: 80,
        weight: 0,
        alignment: "left"
      },
      {
        key: "argument.user",
        title: "User",
        createCell: ({rowData}) => ({
          type: "text",
          value: DamlLfValue.toJSON(rowData.argument).user
        }),
        sortable: true,
        width: 80,
        weight: 0,
        alignment: "left"
      },
      {
        key: "argument.operator",
        title: "Operator",
        createCell: ({rowData}) => ({
          type: "text",
          value: DamlLfValue.toJSON(rowData.argument).operator
        }),
        sortable: true,
        width: 80,
        weight: 0,
        alignment: "left"
      }
    ]
  },
})
